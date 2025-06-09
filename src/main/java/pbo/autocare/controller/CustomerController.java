// src/main/java/pbo/autocare/controller/CustomerController.java

package pbo.autocare.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired; // Tambahkan import ini
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import pbo.autocare.dto.ChangePasswordDTO;
import pbo.autocare.dto.ServiceOrderFormDTO;
import pbo.autocare.model.Customer;
import pbo.autocare.model.ServiceItem;
import pbo.autocare.model.ServiceOrder;
import pbo.autocare.model.Transaction;
import pbo.autocare.model.User;
import pbo.autocare.model.Vehicle;
import pbo.autocare.repository.ServiceItemRepository;
import pbo.autocare.repository.TransactionRepository;
import pbo.autocare.repository.UserRepository;
import pbo.autocare.repository.VehicleRepository;
import pbo.autocare.service.ServiceOrderService;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private ServiceOrderService serviceOrderService;

    @Autowired
    private ServiceItemRepository serviceItemRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private pbo.autocare.repository.ServiceOrderRepository serviceOrderRepository;

    @Autowired // <-- TAMBAHKAN INI UNTUK INJEKSI PASSWORDENCODER
    private PasswordEncoder passwordEncoder;

    @GetMapping("/dashboard")
    public String customerDashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "customer_dashboard";
    }

    @GetMapping("/new-reservation")
    public String newTransactionForm(Authentication authentication, Model model) {
        ServiceOrderFormDTO dto = new ServiceOrderFormDTO();

        // Tidak perlu set userId ke DTO lagi jika kita akan mengambilnya langsung dari Authentication
        // Namun, jika form HTML Anda memiliki th:field="*{userId}" (input hidden), biarkan saja.
        // Spring akan mengisi DTO.userId dengan null jika tidak ada input hidden.
        // Yang penting, kita tidak AKAN MENGANDALKAN DTO.userId di metode POST.

        model.addAttribute("serviceOrderFormDTO", dto);
        model.addAttribute("vehicles", serviceOrderService.getAllVehicles());
        model.addAttribute("services", serviceOrderService.getAllServices());
        return "service_order_form";
    }

    @PostMapping("/save-reservation")
    public String saveNewReservation(
            @ModelAttribute("serviceOrderFormDTO") @Valid ServiceOrderFormDTO serviceOrderFormDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Authentication authentication,
            Model model) { // Model perlu ditambahkan sebagai parameter

        // --- BAGIAN SENSITIF 1: PENGECEKAN VALIDASI ---
        if (bindingResult.hasErrors()) {
            // Jika ada error validasi, masuk ke sini
            System.out.println("--- Validation Errors Found! ---"); // Debug ini untuk melihat di log
            bindingResult.getAllErrors().forEach(error -> {
                // Debugging yang lebih detail untuk melihat error spesifik
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    System.out.println("Validation Error: Field '" + fieldError.getField() + "' - Message: " + fieldError.getDefaultMessage() + " - Rejected Value: " + fieldError.getRejectedValue());
                } else {
                    System.out.println("Validation Error: Object '" + error.getObjectName() + "' - Message: " + error.getDefaultMessage());
                }
            });
            // --- PENTING: PASTIKAN SEMUA DATA DROPDOWN DIKEMBALIKAN KE MODEL ---
            model.addAttribute("vehicles", serviceOrderService.getAllVehicles());
            model.addAttribute("services", serviceOrderService.getAllServices());
            model.addAttribute("serviceOrderFormDTO", serviceOrderFormDTO); // DTO dengan data yang sudah diisi pengguna + error
            return "service_order_form"; // Render ulang form yang sama
        }

        try {
            System.out.println("--- No Validation Errors, proceeding to save logic ---"); // Debug ini jika validasi sukses

            User currentUser = null;
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                String username = ((UserDetails) authentication.getPrincipal()).getUsername();
                Optional<User> userOptional = userRepository.findByUsername(username);
                if (userOptional.isPresent()) {
                    currentUser = userOptional.get();
                }
            }

            if (currentUser == null) {
                System.out.println("--- User not logged in or invalid session ---"); // Debug ini
                redirectAttributes.addFlashAttribute("errorMessage", "Anda harus login untuk membuat reservasi.");
                return "redirect:/login";
            }

            // --- BAGIAN SENSITIF 2: PENCARIAN ENTITAS TERKAIT ---
            // Ini adalah tempat di mana NullPointerException atau IllegalArgumentException bisa terjadi
            // jika ID kendaraan atau layanan yang dikirim dari form tidak valid/tidak ditemukan di DB.
            Optional<Vehicle> vehicleOptional = vehicleRepository.findById(serviceOrderFormDTO.getVehicleTypeId());
            Optional<ServiceItem> serviceOptional = serviceItemRepository.findById(serviceOrderFormDTO.getServiceId());

            if (vehicleOptional.isPresent() && serviceOptional.isPresent()) {
                System.out.println("--- Vehicle and Service Found, converting DTO to ServiceOrder ---"); // Debug ini
                ServiceItem selectedService = serviceOptional.get();
                Vehicle selectedVehicle = vehicleOptional.get();

                BigDecimal finalPrice = selectedService.calculateFinalPrice(selectedVehicle);

                ServiceOrder serviceOrder = new ServiceOrder();
                serviceOrder.setUser(currentUser);
                serviceOrder.setCustomerName(serviceOrderFormDTO.getCustomerName());
                serviceOrder.setCustomerContact(serviceOrderFormDTO.getCustomerContact());
                serviceOrder.setCustomerAddress(serviceOrderFormDTO.getCustomerAddress());
                serviceOrder.setVehicleModelName(serviceOrderFormDTO.getVehicleModelName());
                serviceOrder.setVehicleType(selectedVehicle); // Set objek Vehicle
                serviceOrder.setLicensePlate(serviceOrderFormDTO.getLicensePlate());
                serviceOrder.setService(selectedService); // Set objek ServiceItem
                serviceOrder.setServiceName(selectedService.getServiceName());
                serviceOrder.setFinalPrice(finalPrice);
                serviceOrder.setSelectedDurationDays(serviceOrderFormDTO.getDurationDays()); // Pastikan nama ini cocok dengan DTO
                serviceOrder.setOrderNotes(serviceOrderFormDTO.getOrderNotes());

                // --- BAGIAN SENSITIF 3: PEMANGGILAN SERVICE UNTUK MENYIMPAN ---
                // Ini adalah tempat di mana operasi penyimpanan sebenarnya dilakukan
                serviceOrderService.saveServiceOrder(serviceOrder);
                System.out.println("--- Service Order SAVED to database! ---"); // Debug ini jika berhasil

                redirectAttributes.addFlashAttribute("successMessage", "Reservasi berhasil dibuat!");
                return "redirect:/customer/list-reservations";
            } else {
                // Ini akan terpanggil jika vehicleId atau serviceId dari form tidak ditemukan di DB
                System.out.println("--- Vehicle or Service NOT Found (IDs from DTO were invalid) ---"); // Debug ini
                redirectAttributes.addFlashAttribute("errorMessage", "Error: Kendaraan atau Layanan tidak ditemukan. Mohon coba lagi.");
                // Pastikan Anda mengembalikan semua model attributes yang dibutuhkan form jika kembali ke form yang sama
                model.addAttribute("serviceOrderFormDTO", serviceOrderFormDTO);
                model.addAttribute("vehicles", serviceOrderService.getAllVehicles());
                model.addAttribute("services", serviceOrderService.getAllServices());
                return "service_order_form"; // Render ulang form yang sama
            }
        } catch (Exception e) {
            // --- BAGIAN SENSITIF 4: PENANGANAN EXCEPTION UMUM ---
            // Jika ada exception lain (misal dari ServiceOrderService.saveServiceOrder()), akan tertangkap di sini
            System.err.println("--- General Exception during save: ---"); // Debug ini
            e.printStackTrace(); // PRINT STACK TRACE LENGKAP
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan server: " + e.getMessage());
            // Pastikan Anda mengembalikan semua model attributes yang dibutuhkan form jika kembali ke form yang sama
            model.addAttribute("serviceOrderFormDTO", serviceOrderFormDTO);
            model.addAttribute("vehicles", serviceOrderService.getAllVehicles());
            model.addAttribute("services", serviceOrderService.getAllServices());
            return "service_order_form"; // Render ulang form yang sama
        }
}

     @GetMapping("/list-reservations")
    public String customerListReservations(Authentication authentication, Model model) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            Optional<User> userOptional = userRepository.findByUsername(username);

            if (userOptional.isPresent()) {
                User currentUser = userOptional.get();
                List<ServiceOrder> customerOrders = serviceOrderRepository.findByUser(currentUser);
                model.addAttribute("orders", customerOrders);
            } else {
                model.addAttribute("orders", new java.util.ArrayList<ServiceOrder>());
                model.addAttribute("errorMessage", "Data pengguna tidak ditemukan.");
            }
        } else {
            return "redirect:/login";
        }
        return "customer/orderlist"; // Mengarahkan ke customer/orderlist.html
    }

    @GetMapping("/profile")
    public String showProfile(Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Anda harus login untuk melihat profil.");
            return "redirect:/login";
        }

        String username = authentication.getName();

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User currentUser = userOptional.get();
            if (currentUser instanceof Customer) {
                model.addAttribute("customer", (Customer) currentUser);
                if (model.asMap().containsKey("updateSuccess")) {
                    model.addAttribute("successMessage", model.asMap().get("updateSuccess"));
                }
                return "customer/profile";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Tipe akun tidak diizinkan untuk melihat profil ini.");
                return "redirect:/customer/dashboard";
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Data pengguna tidak ditemukan. Mohon login kembali.");
            return "redirect:/login";
        }
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("customer") @Valid Customer updatedCustomer,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
             boolean hasRelevantErrors = bindingResult.getFieldErrors().stream()
                 .anyMatch(fe -> fe.getField().equals("fullName") || fe.getField().equals("phoneNumber"));

             if (hasRelevantErrors) {
                 redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.customer", bindingResult);
                 redirectAttributes.addFlashAttribute("customer", updatedCustomer);
                 redirectAttributes.addFlashAttribute("errorMessage", "Data profil tidak valid. Mohon periksa kembali input Anda.");
                 return "redirect:/customer/profile";
             }
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sesi Anda telah berakhir. Mohon login kembali.");
            return "redirect:/login";
        }

        String username = authentication.getName();

        Optional<User> existingUserOptional = userRepository.findByUsername(username);

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            if (existingUser instanceof Customer) {
                Customer currentCustomer = (Customer) existingUser;

                // ***** Hanya update field yang diizinkan *****
                currentCustomer.setFullName(updatedCustomer.getFullName());
                currentCustomer.setPhoneNumber(updatedCustomer.getPhoneNumber());
                // Jangan update currentCustomer.setUsername(updatedCustomer.getUsername());
                // Jangan update currentCustomer.setEmail(updatedCustomer.getEmail());
                // Jangan update currentCustomer.setPassword(updatedCustomer.getPassword());
                // Jangan update currentCustomer.setId(updatedCustomer.getId());

                userRepository.save(currentCustomer);
                redirectAttributes.addFlashAttribute("updateSuccess", "Profil Anda berhasil diperbarui!");
                return "redirect:/customer/profile";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Tipe akun tidak diizinkan untuk memperbarui profil ini.");
                return "redirect:/customer/dashboard";
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal memperbarui profil. Data pengguna tidak ditemukan.");
            return "redirect:/customer/profile";
        }
    }

    @GetMapping("/profile/change-password") // URL akan menjadi /customer/change-password
    public String showChangePasswordForm(Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Anda harus login untuk mengubah password.");
            return "redirect:/login";
        }

        // Pastikan model memiliki objek DTO untuk form
        if (!model.containsAttribute("changePasswordDTO")) { // Menggunakan changePasswordDTO
            model.addAttribute("changePasswordDTO", new ChangePasswordDTO()); // Menggunakan ChangePasswordDTO
        }

        return "customer/change_password"; // Nama file Thymeleaf: customer/change_password.html (dengan underscore)
    }

    @PostMapping("/profile/change-password") // URL akan menjadi /customer/change-password
    public String changePassword(@ModelAttribute("changePasswordDTO") @Valid ChangePasswordDTO changePasswordDTO, // Menggunakan ChangePasswordDTO
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sesi Anda telah berakhir. Mohon login kembali.");
            return "redirect:/login";
        }

        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Pengguna tidak ditemukan.");
            return "redirect:/customer/profile/change-password"; // Redirect ke GET request yang sama
        }

        User currentUser = userOptional.get();

        // 1. Validasi BindingResult (dari @Valid di DTO)
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordDTO", bindingResult); // Konsisten
            redirectAttributes.addFlashAttribute("changePasswordDTO", changePasswordDTO); // Konsisten
            redirectAttributes.addFlashAttribute("errorMessage", "Silakan periksa kembali input Anda.");
            return "redirect:/customer/profile/change-password"; // Redirect ke GET request yang sama
        }

        // 2. Verifikasi Password Lama
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), currentUser.getPassword())) {
            bindingResult.addError(new FieldError("changePasswordDTO", "oldPassword", "Password lama salah.")); // Konsisten
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordDTO", bindingResult); // Konsisten
            redirectAttributes.addFlashAttribute("changePasswordDTO", changePasswordDTO); // Konsisten
            redirectAttributes.addFlashAttribute("errorMessage", "Password lama yang Anda masukkan salah.");
            return "redirect:/customer/profile/change-password"; // Redirect ke GET request yang sama
        }

        // 3. Verifikasi Password Baru dan Konfirmasi Password
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            bindingResult.addError(new FieldError("changePasswordDTO", "confirmPassword", "Konfirmasi password tidak cocok dengan password baru.")); // Konsisten
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordDTO", bindingResult); // Konsisten
            redirectAttributes.addFlashAttribute("changePasswordDTO", changePasswordDTO); // Konsisten
            redirectAttributes.addFlashAttribute("errorMessage", "Konfirmasi password baru tidak cocok.");
            return "redirect:/customer/profile/change-password"; // Redirect ke GET request yang sama
        }

        // 4. Pastikan password baru tidak sama dengan password lama (opsional tapi bagus)
        if (passwordEncoder.matches(changePasswordDTO.getNewPassword(), currentUser.getPassword())) {
            bindingResult.addError(new FieldError("changePasswordDTO", "newPassword", "Password baru tidak boleh sama dengan password lama.")); // Konsisten
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordDTO", bindingResult); // Konsisten
            redirectAttributes.addFlashAttribute("changePasswordDTO", changePasswordDTO); // Konsisten
            redirectAttributes.addFlashAttribute("errorMessage", "Password baru tidak boleh sama dengan yang sudah ada.");
            return "redirect:/customer/profile/change-password"; // Redirect ke GET request yang sama
        }

        // 5. Enkripsi dan Simpan Password Baru
        currentUser.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(currentUser);

        redirectAttributes.addFlashAttribute("successMessage", "Password Anda berhasil diubah!");
        return "redirect:/customer/profile"; // Redirect kembali ke halaman profil setelah sukses
    }

    // Di CustomerController.java (atau AdminServiceOrderController jika hanya admin yang bisa print struk)
    // Asumsi ini adalah controller yang tepat

    @Autowired // Pastikan Anda sudah mengautowire TransactionRepository
    private TransactionRepository transactionRepository; // Anda perlu membuat ini jika belum ada

    // Endpoint untuk menampilkan struk dari sebuah Transaction
    // Dalam CustomerController.java

// Endpoint ini tetap menerima ServiceOrder ID
@GetMapping("/reservations/print/{serviceOrderId}")
public String printReservationReceipt(@PathVariable Long serviceOrderId, Model model, RedirectAttributes redirectAttributes) {
    Optional<ServiceOrder> serviceOrderOptional = serviceOrderService.getServiceOrderById(serviceOrderId); // Cari ServiceOrder

    if (serviceOrderOptional.isPresent()) {
        ServiceOrder serviceOrder = serviceOrderOptional.get();
        // Cari Transaction yang terkait dengan ServiceOrder ini
        // Anda perlu membuat metode di TransactionRepository seperti findByServiceOrder(ServiceOrder serviceOrder)
        Optional<Transaction> transactionOptional = transactionRepository.findByServiceOrder(serviceOrder);

        if (transactionOptional.isPresent()) {
            Transaction transaction = transactionOptional.get();
            model.addAttribute("transaction", transaction); // Pass objek Transaction ke view
            model.addAttribute("currentDate", new java.util.Date());
            return "customer/print_receipt";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Transaksi untuk order ini tidak ditemukan.");
            return "redirect:/customer/list-reservations";
        }
    } else {
        redirectAttributes.addFlashAttribute("errorMessage", "Order tidak ditemukan.");
        return "redirect:/customer/list-reservations";
    }
}
}