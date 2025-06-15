package pbo.autocare.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired; 
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

    @Autowired 
    private PasswordEncoder passwordEncoder;

    @GetMapping("/dashboard")
    public String customerDashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "customer_dashboard";
    }

    @GetMapping("/new-reservation")
    public String newTransactionForm(Authentication authentication, Model model) {
        ServiceOrderFormDTO dto = new ServiceOrderFormDTO();

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
            Model model) {

        if (bindingResult.hasErrors()) {

            System.out.println("--- Validation Errors Found! ---");
            bindingResult.getAllErrors().forEach(error -> {

                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    System.out.println("Validation Error: Field '" + fieldError.getField() + "' - Message: " + fieldError.getDefaultMessage() + " - Rejected Value: " + fieldError.getRejectedValue());
                } else {
                    System.out.println("Validation Error: Object '" + error.getObjectName() + "' - Message: " + error.getDefaultMessage());
                }
            });

            model.addAttribute("vehicles", serviceOrderService.getAllVehicles());
            model.addAttribute("services", serviceOrderService.getAllServices());
            model.addAttribute("serviceOrderFormDTO", serviceOrderFormDTO); 
            return "service_order_form"; 
        }

        try {
            System.out.println("--- No Validation Errors, proceeding to save logic ---"); 

            User currentUser = null;
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                String username = ((UserDetails) authentication.getPrincipal()).getUsername();
                Optional<User> userOptional = userRepository.findByUsername(username);
                if (userOptional.isPresent()) {
                    currentUser = userOptional.get();
                }
            }

            if (currentUser == null) {
                System.out.println("--- User not logged in or invalid session ---");
                redirectAttributes.addFlashAttribute("errorMessage", "Anda harus login untuk membuat reservasi.");
                return "redirect:/login";
            }

            Optional<Vehicle> vehicleOptional = vehicleRepository.findById(serviceOrderFormDTO.getVehicleTypeId());
            Optional<ServiceItem> serviceOptional = serviceItemRepository.findById(serviceOrderFormDTO.getServiceId());

            if (vehicleOptional.isPresent() && serviceOptional.isPresent()) {
                System.out.println("--- Vehicle and Service Found, converting DTO to ServiceOrder ---");
                ServiceItem selectedService = serviceOptional.get();
                Vehicle selectedVehicle = vehicleOptional.get();

                BigDecimal finalPrice = selectedService.calculateFinalPrice(selectedVehicle);

                ServiceOrder serviceOrder = new ServiceOrder();
                serviceOrder.setUser(currentUser);
                serviceOrder.setCustomerName(serviceOrderFormDTO.getCustomerName());
                serviceOrder.setCustomerContact(serviceOrderFormDTO.getCustomerContact());
                serviceOrder.setCustomerAddress(serviceOrderFormDTO.getCustomerAddress());
                serviceOrder.setVehicleModelName(serviceOrderFormDTO.getVehicleModelName());
                serviceOrder.setVehicleType(selectedVehicle);
                serviceOrder.setLicensePlate(serviceOrderFormDTO.getLicensePlate());
                serviceOrder.setService(selectedService);
                serviceOrder.setServiceName(selectedService.getServiceName());
                serviceOrder.setFinalPrice(finalPrice);
                serviceOrder.setSelectedDurationDays(serviceOrderFormDTO.getDurationDays());
                serviceOrder.setOrderNotes(serviceOrderFormDTO.getOrderNotes());

                serviceOrderService.saveServiceOrder(serviceOrder);
                System.out.println("--- Service Order SAVED to database! ---"); 

                redirectAttributes.addFlashAttribute("successMessage", "Reservasi berhasil dibuat!");
                return "redirect:/customer/list-reservations";
            } else {

                System.out.println("--- Vehicle or Service NOT Found (IDs from DTO were invalid) ---");
                redirectAttributes.addFlashAttribute("errorMessage", "Error: Kendaraan atau Layanan tidak ditemukan. Mohon coba lagi.");

                model.addAttribute("serviceOrderFormDTO", serviceOrderFormDTO);
                model.addAttribute("vehicles", serviceOrderService.getAllVehicles());
                model.addAttribute("services", serviceOrderService.getAllServices());
                return "service_order_form"; 
            }
        } catch (Exception e) {

            System.err.println("--- General Exception during save: ---"); 
            e.printStackTrace(); 
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan server: " + e.getMessage());

            model.addAttribute("serviceOrderFormDTO", serviceOrderFormDTO);
            model.addAttribute("vehicles", serviceOrderService.getAllVehicles());
            model.addAttribute("services", serviceOrderService.getAllServices());
            return "service_order_form";
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
        return "customer/orderlist"; 
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

                currentCustomer.setFullName(updatedCustomer.getFullName());
                currentCustomer.setPhoneNumber(updatedCustomer.getPhoneNumber());

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

    @GetMapping("/profile/change-password") 
    public String showChangePasswordForm(Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Anda harus login untuk mengubah password.");
            return "redirect:/login";
        }

        if (!model.containsAttribute("changePasswordDTO")) { 
            model.addAttribute("changePasswordDTO", new ChangePasswordDTO()); 
        }

        return "customer/change_password"; 
    }

    @PostMapping("/profile/change-password") 
    public String changePassword(@ModelAttribute("changePasswordDTO") @Valid ChangePasswordDTO changePasswordDTO, 
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
            return "redirect:/customer/profile/change-password"; 
        }

        User currentUser = userOptional.get();

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordDTO", bindingResult); 
            redirectAttributes.addFlashAttribute("changePasswordDTO", changePasswordDTO); 
            redirectAttributes.addFlashAttribute("errorMessage", "Silakan periksa kembali input Anda.");
            return "redirect:/customer/profile/change-password"; 
        }

        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), currentUser.getPassword())) {
            bindingResult.addError(new FieldError("changePasswordDTO", "oldPassword", "Password lama salah.")); 
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordDTO", bindingResult); 
            redirectAttributes.addFlashAttribute("changePasswordDTO", changePasswordDTO); 
            redirectAttributes.addFlashAttribute("errorMessage", "Password lama yang Anda masukkan salah.");
            return "redirect:/customer/profile/change-password"; 
        }

        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            bindingResult.addError(new FieldError("changePasswordDTO", "confirmPassword", "Konfirmasi password tidak cocok dengan password baru."));
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordDTO", bindingResult);
            redirectAttributes.addFlashAttribute("changePasswordDTO", changePasswordDTO);
            redirectAttributes.addFlashAttribute("errorMessage", "Konfirmasi password baru tidak cocok.");
            return "redirect:/customer/profile/change-password"; 
        }

        if (passwordEncoder.matches(changePasswordDTO.getNewPassword(), currentUser.getPassword())) {
            bindingResult.addError(new FieldError("changePasswordDTO", "newPassword", "Password baru tidak boleh sama dengan password lama.")); 
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordDTO", bindingResult); 
            redirectAttributes.addFlashAttribute("changePasswordDTO", changePasswordDTO);
            redirectAttributes.addFlashAttribute("errorMessage", "Password baru tidak boleh sama dengan yang sudah ada.");
            return "redirect:/customer/profile/change-password";
        }

        currentUser.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(currentUser);

        redirectAttributes.addFlashAttribute("successMessage", "Password Anda berhasil diubah!");
        return "redirect:/customer/profile"; 
    }

    @Autowired 
    private TransactionRepository transactionRepository;

@GetMapping("/reservations/print/{serviceOrderId}")
public String printReservationReceipt(@PathVariable Long serviceOrderId, Model model, RedirectAttributes redirectAttributes) {
    Optional<ServiceOrder> serviceOrderOptional = serviceOrderService.getServiceOrderById(serviceOrderId); 

    if (serviceOrderOptional.isPresent()) {
        ServiceOrder serviceOrder = serviceOrderOptional.get();

        Optional<Transaction> transactionOptional = transactionRepository.findByServiceOrder(serviceOrder);

        if (transactionOptional.isPresent()) {
            Transaction transaction = transactionOptional.get();
            model.addAttribute("transaction", transaction);
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