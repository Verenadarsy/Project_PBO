package pbo.autocare.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Pastikan ini di-import
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // Tetap sertakan jika ada @PostMapping
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import pbo.autocare.dto.AdminServiceOrderFormDTO;
import pbo.autocare.dto.CustomerFormDTO;
import pbo.autocare.dto.SpecializationListDTO;
import pbo.autocare.dto.TechnicianFormDTO;
import pbo.autocare.model.Customer;
import pbo.autocare.model.Generalservice;
import pbo.autocare.model.ServiceItem;
import pbo.autocare.model.ServiceOrder;
import pbo.autocare.model.Specialization;
import pbo.autocare.model.SpecializedService;
import pbo.autocare.model.Staff;
import pbo.autocare.model.Technician;
import pbo.autocare.model.Transaction;
import pbo.autocare.model.User;
import pbo.autocare.model.Vehicle;
import pbo.autocare.repository.ServiceOrderRepository;
import pbo.autocare.repository.SpecializationRepository;
import pbo.autocare.repository.UserRepository;
import pbo.autocare.service.CustomerService;
import pbo.autocare.service.ServiceItemService;
import pbo.autocare.service.ServiceOrderService;
import pbo.autocare.service.SpecializationService;
import pbo.autocare.service.TransactionService;
import pbo.autocare.service.UserServiceImpl;
import pbo.autocare.service.VehicleService; 

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserServiceImpl userService;

    @Autowired
    private CustomerService customerService;

    private SpecializationService specializationService;

    public AdminController(UserServiceImpl userService, TransactionService transactionService, SpecializationService specializationService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.specializationService = specializationService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {

        long totalCustomers = userService.countCustomers();
        long totalTechnicians = userService.countTechnicians();
        long totalStaff = userService.countStaff();
        long ordersThisMonth = userService.countOrdersThisMonth();

        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalTechnicians", totalTechnicians);
        model.addAttribute("totalStaff", totalStaff);
        model.addAttribute("ordersThisMonth", ordersThisMonth);

        return "admin_dashboard";
    }

    @GetMapping("/customers")
    public String listCustomers(Model model) {
        List<Customer> customers = userService.getAllCustomers(); 
        model.addAttribute("customers", customers);

        model.addAttribute("pageTitle", "Daftar Pelanggan (Admin)");
        model.addAttribute("headerTitle", "Daftar Pelanggan (Admin)");
        model.addAttribute("backLink", "/admin/dashboard");
        model.addAttribute("backText", "Kembali ke Dashboard Admin");

        return "admin/customer_list"; 
    }

    @GetMapping("/customers/new")
    public String showAddCustomerForm(Model model) {
        model.addAttribute("customerFormDTO", new CustomerFormDTO());
        return "admin/EditCustomerForm"; 
    }

    @GetMapping("/customers/edit/{id}") 
    public String showEditCustomerForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Customer> customer = customerService.findCustomerById(id);
        if (customer.isPresent()) {
            CustomerFormDTO customerFormDTO = new CustomerFormDTO(
                customer.get().getId(),
                customer.get().getUsername(),
                null,
                customer.get().getEmail(),
                customer.get().getFullName(),
                customer.get().getPhoneNumber()
            );
            model.addAttribute("customerFormDTO", customerFormDTO);
            return "admin/EditCustomerForm"; 
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Pelanggan tidak ditemukan untuk diedit.");
            return "redirect:/admin/customers"; 
        }
    }

    @PostMapping("/customers/save")
    public String saveCustomer(@ModelAttribute("customerFormDTO") @Valid CustomerFormDTO customerFormDTO,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {

        if (customerFormDTO.getId() == null &&
            (customerFormDTO.getPassword() == null || customerFormDTO.getPassword().isEmpty())) {
            bindingResult.rejectValue("password", "error.customerFormDTO", "Password tidak boleh kosong saat pendaftaran");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.customerFormDTO", bindingResult);
            redirectAttributes.addFlashAttribute("customerFormDTO", customerFormDTO);
            if (customerFormDTO.getId() == null) {
                return "redirect:/admin/customers/new";
            } else {
                return "redirect:/admin/customers/edit/" + customerFormDTO.getId();
            }
        }

        try {
            Customer customer;
            if (customerFormDTO.getId() == null) {
                customer = new Customer();
                customer.setUsername(customerFormDTO.getUsername());
                customer.setPassword(customerFormDTO.getPassword());
                customer.setEmail(customerFormDTO.getEmail());
                customer.setFullName(customerFormDTO.getFullName());
                customer.setPhoneNumber(customerFormDTO.getPhoneNumber());
            } else {
                Optional<Customer> existingCustomer = customerService.findCustomerById(customerFormDTO.getId());
                if (existingCustomer.isPresent()) {
                    customer = existingCustomer.get();
                    customer.setUsername(customerFormDTO.getUsername());
                    if (customerFormDTO.getPassword() != null && !customerFormDTO.getPassword().isEmpty()) {
                        customer.setPassword(customerFormDTO.getPassword());
                    }
                    customer.setEmail(customerFormDTO.getEmail());
                    customer.setFullName(customerFormDTO.getFullName());
                    customer.setPhoneNumber(customerFormDTO.getPhoneNumber());
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Pelanggan tidak ditemukan untuk diedit.");
                    return "redirect:/admin/customers";
                }
            }

            customerService.saveCustomer(customer);
            redirectAttributes.addFlashAttribute("successMessage", "Pelanggan berhasil disimpan!");
            return "redirect:/admin/customers";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan saat menyimpan pelanggan: " + e.getMessage());
            redirectAttributes.addFlashAttribute("customerFormDTO", customerFormDTO);
            if (customerFormDTO.getId() == null) {
                return "redirect:/admin/customers/new";
            } else {
                return "redirect:/admin/customers/edit/" + customerFormDTO.getId();
            }
        }
    }

    @PostMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomerById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Pelanggan berhasil dihapus!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan saat menghapus pelanggan: " + e.getMessage());
        }
        return "redirect:/admin/customers";
    }

    @GetMapping("/technician")
    public String listTech(Model model) {
        List<User> techList = userService.getAllTechnicians();
        model.addAttribute("technicians", techList);
        model.addAttribute("pageTitle", "Daftar Teknisi (Admin)");
        model.addAttribute("headerTitle", "Daftar Teknisi (Admin)");
        model.addAttribute("backLink", "/admin/dashboard");
        model.addAttribute("backText", "Kembali ke Dashboard Admin");
        return "technician_list";
    }
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SpecializationRepository specializationRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // ===========================================
    // Create (Add) Teknisi
    // URL: /admin/technicians/new
    // ===========================================
    @GetMapping("/technician/new")
    public String showAddTechnicianForm(Model model) {
        if (!model.containsAttribute("technicianFormDTO")) {
             model.addAttribute("technicianFormDTO", new TechnicianFormDTO());
        }
        model.addAttribute("specializations", specializationRepository.findAll());
        return "admin/EditTechForm"; 
    }

    // ===========================================
    // Save (Create/Update) Teknisi
    // URL: /admin/technicians/save
    // ===========================================
    @PostMapping("/technician/save") // Hanya satu endpoint POST untuk menyimpan
    public String saveTechnician(@ModelAttribute("technicianFormDTO") @Valid TechnicianFormDTO technicianFormDTO,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {

        // --- Log error awal dari @Valid DTO ---
        if (bindingResult.hasErrors()) {
            System.out.println("--- INITIAL @VALID DTO ERRORS ---");
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
            bindingResult.getFieldErrors().forEach(error -> System.out.println("Field: " + error.getField() + ", Error: " + error.getDefaultMessage()));
            System.out.println("--- END INITIAL @VALID DTO ERRORS ---");
        }

        // Ambil objek Technician yang ada (jika ini mode edit)
        Technician currentTechnicianInDb = null;
        if (technicianFormDTO.getId() != null) {
            Optional<Technician> existingTechnicianOpt = userRepository.findTechnicianById(technicianFormDTO.getId());
            if (existingTechnicianOpt.isPresent()) {
                currentTechnicianInDb = existingTechnicianOpt.get();
            } else {
                bindingResult.rejectValue("id", "NotFound", "Teknisi dengan ID ini tidak ditemukan.");
            }
        }

        // --- Logika Validasi Kustom Tambahan ---
        // 1. Validasi Password
        if (technicianFormDTO.getId() == null) { // Mode Create
            if (technicianFormDTO.getPassword() == null || technicianFormDTO.getPassword().isEmpty()) {
                bindingResult.rejectValue("password", "NotBlank", "Password tidak boleh kosong untuk akun baru.");
            }
        }
        // Catatan: Untuk mode edit, @Size di DTO sudah menangani jika password diisi tapi terlalu pendek.
        //           Jika password kosong di form edit, @Size tidak memicu error, yang memang kita inginkan.

        // 2. Validasi Duplikasi Username
        // Username sekarang bisa diedit.
        if (userRepository.findByUsername(technicianFormDTO.getUsername()).isPresent()) {
            User userWithSameUsername = userRepository.findByUsername(technicianFormDTO.getUsername()).get();
            if (technicianFormDTO.getId() == null || !userWithSameUsername.getId().equals(technicianFormDTO.getId())) {
                 // Jika ini mode create, atau mode update tapi username berbeda dan sudah ada yang punya
                bindingResult.rejectValue("username", "Duplicate", "Username sudah terdaftar.");
            }
        }

        // 3. Validasi Duplikasi Email
        if (userRepository.findByEmail(technicianFormDTO.getEmail()).isPresent()) {
            User userWithSameEmail = userRepository.findByEmail(technicianFormDTO.getEmail()).get();
            if (technicianFormDTO.getId() == null || !userWithSameEmail.getId().equals(technicianFormDTO.getId())) {
                 // Jika ini mode create, atau mode update tapi email berbeda dan sudah ada yang punya
                bindingResult.rejectValue("email", "Duplicate", "Email sudah terdaftar.");
            }
        }
        // --- AKHIR Validasi Kustom Tambahan ---


        // --- Periksa BindingResult setelah SEMUA validasi (@Valid DTO dan kustom) ---
        if (bindingResult.hasErrors()) {
            System.out.println("--- FINAL VALIDATION ERRORS (all checks) ---");
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
            bindingResult.getFieldErrors().forEach(error -> System.out.println("Field: " + error.getField() + ", Error: " + error.getDefaultMessage()));
            System.out.println("--- END FINAL VALIDATION ERRORS ---");

            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.technicianFormDTO", bindingResult);
            redirectAttributes.addFlashAttribute("technicianFormDTO", technicianFormDTO); // Kirim kembali DTO yang diisi user
            redirectAttributes.addFlashAttribute("errorMessage", "Data teknisi tidak valid. Mohon periksa kembali input Anda.");
            redirectAttributes.addFlashAttribute("specializations", specializationRepository.findAll()); // Kirim ulang daftar spesialisasi

            String redirectToUrl = (technicianFormDTO.getId() == null) ? "/admin/technician/new" : "/admin/technician/edit/" + technicianFormDTO.getId();
            return "redirect:" + redirectToUrl;
        }

        // --- Logika Penyimpanan Jika Tidak Ada Error ---
        // Validasi Spesialisasi ID (safety check jika ID tidak valid di DB)
        Optional<Specialization> specializationOptional = specializationRepository.findById(technicianFormDTO.getSpecializationId());
        if (!specializationOptional.isPresent()) {
            bindingResult.rejectValue("specializationId", "NotFound", "Spesialisasi yang dipilih tidak ditemukan.");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.technicianFormDTO", bindingResult);
            redirectAttributes.addFlashAttribute("technicianFormDTO", technicianFormDTO);
            redirectAttributes.addFlashAttribute("errorMessage", "Data teknisi tidak valid. Spesialisasi tidak valid.");
            redirectAttributes.addFlashAttribute("specializations", specializationRepository.findAll());
            String redirectToUrl = (technicianFormDTO.getId() == null) ? "/admin/technician/new" : "/admin/technician/edit/" + technicianFormDTO.getId();
            return "redirect:" + redirectToUrl;
        }

        Technician technicianToSave;

        if (technicianFormDTO.getId() == null) { // Mode Create
            technicianToSave = new Technician();
            technicianToSave.setUsername(technicianFormDTO.getUsername()); // Set username saat create
            technicianToSave.setPassword(passwordEncoder.encode(technicianFormDTO.getPassword())); // Enkripsi password
            redirectAttributes.addFlashAttribute("successMessage", "Teknisi berhasil ditambahkan!");
        } else { // Mode Update
            if (currentTechnicianInDb == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Teknisi tidak ditemukan untuk diedit.");
                return "redirect:/admin/technician";
            }
            technicianToSave = currentTechnicianInDb;

            // ***** SALIN SEMUA FIELD DARI DTO KE ENTITAS YANG ADA *****
            // Username: Karena sekarang bisa diubah, salin dari DTO.
            technicianToSave.setUsername(technicianFormDTO.getUsername());
            // Password: Salin kondisional.
            if (technicianFormDTO.getPassword() != null && !technicianFormDTO.getPassword().isEmpty()) {
                technicianToSave.setPassword(passwordEncoder.encode(technicianFormDTO.getPassword()));
            }
            // Email, FullName, PhoneNumber: Salin dari DTO.
            technicianToSave.setEmail(technicianFormDTO.getEmail());
            technicianToSave.setFullName(technicianFormDTO.getFullName());
            technicianToSave.setPhoneNumber(technicianFormDTO.getPhoneNumber());
            // **********************************************************

            redirectAttributes.addFlashAttribute("successMessage", "Teknisi berhasil diperbarui!");
        }

        // Set specialization (dilakukan untuk create dan update)
        technicianToSave.setSpecialization(specializationOptional.get());

        userRepository.save(technicianToSave);

        return "redirect:/admin/technician";

    }

    // ===========================================
    // Update (Edit) Teknisi
    // URL: /admin/technician/edit/{id}
    // ===========================================
    @GetMapping("/technician/edit/{id}")
    public String showEditTechnicianForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Technician> technicianOptional = userRepository.findTechnicianById(id);
        if (technicianOptional.isPresent()) {
            Technician technician = technicianOptional.get();
            TechnicianFormDTO dto = new TechnicianFormDTO();
            dto.setId(technician.getId());
            dto.setUsername(technician.getUsername());
            dto.setEmail(technician.getEmail());
            dto.setFullName(technician.getFullName());
            dto.setPhoneNumber(technician.getPhoneNumber());
            if (technician.getSpecialization() != null) {
                dto.setSpecializationId(technician.getSpecialization().getId());
            }

            // Gunakan model.addAttribute LANGSUNG karena kita tidak redirect di sini
            model.addAttribute("technicianFormDTO", dto);
            model.addAttribute("specializations", specializationRepository.findAll());

            // Ambil pesan error/success dari redirectAttributes jika ada (setelah POST)
            if (redirectAttributes.getFlashAttributes().containsKey("org.springframework.validation.BindingResult.technicianFormDTO")) {
                model.addAttribute("org.springframework.validation.BindingResult.technicianFormDTO",
                                   redirectAttributes.getFlashAttributes().get("org.springframework.validation.BindingResult.technicianFormDTO"));
            }
            if (redirectAttributes.getFlashAttributes().containsKey("errorMessage")) {
                model.addAttribute("errorMessage", redirectAttributes.getFlashAttributes().get("errorMessage"));
            }
            if (redirectAttributes.getFlashAttributes().containsKey("successMessage")) {
                 model.addAttribute("successMessage", redirectAttributes.getFlashAttributes().get("successMessage"));
            }


            return "admin/EditTechForm"; 
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Teknisi tidak ditemukan.");
            return "redirect:/admin/technician";
        }
    }

    // ===========================================
    // Delete Teknisi
    // URL: /admin/technicians/delete/{id}
    // ===========================================
    @PostMapping("/technician/delete/{id}")
    public String deleteTechnician(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Technician> technicianOptional = userRepository.findTechnicianById(id);
        if (technicianOptional.isPresent()) {
            userRepository.delete(technicianOptional.get());
            redirectAttributes.addFlashAttribute("successMessage", "Teknisi berhasil dihapus!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Teknisi tidak ditemukan.");
        }
        return "redirect:/admin/technician";
    }

    @GetMapping("/staff")
        public String listStaff(Model model) {
            List<User> staffList = userService.getAllStaff();
            model.addAttribute("staffs", staffList);
            model.addAttribute("pageTitle", "Daftar Staff (Admin)");
            model.addAttribute("headerTitle", "Daftar Staff (Admin)");
            model.addAttribute("backLink", "/admin/dashboard");
            model.addAttribute("backText", "Kembali ke Dashboard Admin");
        return "staff_list";
    }

    @GetMapping("/staff/new")
    public String showAddStaffForm(Model model) {
        model.addAttribute("staff", new Staff());
        return "admin/EditStaffForm";
    }

    @PostMapping("/staff/save")
    public String saveStaff(@ModelAttribute("staff") Staff staff, RedirectAttributes redirectAttributes) {
        // UserService akan menyimpan Staff sebagai User dan menangani password encoding
        userService.saveStaff(staff);
        redirectAttributes.addFlashAttribute("message", "Staff account saved successfully!");
        return "redirect:/admin/staff";
    }

    @GetMapping("/staff/edit/{id}")
    public String showEditStaffForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Staff> staff = userService.getStaffById(id); // Menggunakan getStaffById
        if (staff.isPresent()) {
            model.addAttribute("staff", staff.get());
            return "admin/EditStaffForm";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Staff not found!");
            return "redirect:/admin/staff";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteStaff(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        userService.deleteStaff(id); // Menggunakan deleteStaff
        redirectAttributes.addFlashAttribute("message", "Staff account deleted successfully!");
        return "redirect:/admin/staff";
    }

    @Autowired
    private ServiceOrderService serviceOrderService;

    @Autowired
    private VehicleService vehicleService;
    
    @Autowired
    private ServiceItemService serviceItemService;

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @GetMapping("/service-orders") // Or whatever path maps to this list
    public String listServiceOrders(Model model) {
        List<ServiceOrder> serviceOrders = serviceOrderRepository.findAllWithVehicleTypeAndService();
        model.addAttribute("orders", serviceOrders); // Pastikan nama atribut sama dengan di Thymeleaf
        return "admin/ServiceOrderList";
    }

    @GetMapping("/service-orders/new")
    public String showAdminCreateForm(Model model) {
        AdminServiceOrderFormDTO dto = new AdminServiceOrderFormDTO(); // Gunakan DTO baru
        model.addAttribute("serviceOrderFormDTO", dto); // Nama atribut di model tetap sama
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("vehicleTypes", vehicleService.getAllVehicles());
        model.addAttribute("serviceItems", serviceItemService.getAllServiceItems());
        return "admin/EditServiceOrderForm";
    }

    // For editing:
    @PostMapping("/service-orders/save")
    public String saveServiceOrder(@ModelAttribute("serviceOrderFormDTO") @Valid AdminServiceOrderFormDTO serviceOrderFormDTO,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   Model model) { // Tambahkan Model model di sini untuk re-add attributes jika ada error

        // 1. Validasi DTO
        if (bindingResult.hasErrors()) {
            // Jika ada error validasi, tambahkan kembali data yang dibutuhkan form
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("vehicleTypes", vehicleService.getAllVehicles());
            model.addAttribute("serviceItems", serviceItemService.getAllServiceItems());
            return "admin/EditServiceOrderForm"; // Kembali ke form dengan pesan error
        }

        try {
            // Mengambil User dari ID yang dipilih di form (tanpa sesi login)
            User selectedUser = userService.getCustomerById(serviceOrderFormDTO.getUserId())
                                        .orElseThrow(() -> new IllegalArgumentException("User dengan ID " + serviceOrderFormDTO.getUserId() + " tidak ditemukan."));

            Optional<Vehicle> vehicleOptional = vehicleService.getVehicleById(serviceOrderFormDTO.getVehicleTypeId());
            Optional<ServiceItem> serviceOptional = serviceItemService.getServiceItemById(serviceOrderFormDTO.getServiceId());

            if (vehicleOptional.isPresent() && serviceOptional.isPresent()) {
                ServiceItem selectedServiceItem = serviceOptional.get();
                Vehicle selectedVehicle = vehicleOptional.get();

                // Lakukan perhitungan harga akhir
                BigDecimal finalPrice = selectedServiceItem.calculateFinalPrice(selectedVehicle);
                // Set harga akhir ke DTO agar bisa disimpan (jika finalPrice di DTO digunakan untuk submit)
                serviceOrderFormDTO.setFinalPrice(finalPrice);


                ServiceOrder serviceOrder = new ServiceOrder();
                // Jika ini mode edit, set ID dari DTO agar ServiceOrderService tahu itu update
                if (serviceOrderFormDTO.getId() != null) {
                    serviceOrder.setId(serviceOrderFormDTO.getId());
                }

                serviceOrder.setUser(selectedUser); // Set user dari pilihan dropdown
                serviceOrder.setCustomerName(serviceOrderFormDTO.getCustomerName());
                serviceOrder.setCustomerContact(serviceOrderFormDTO.getCustomerContact());
                serviceOrder.setCustomerAddress(serviceOrderFormDTO.getCustomerAddress());
                serviceOrder.setVehicleModelName(serviceOrderFormDTO.getVehicleModelName());
                serviceOrder.setVehicleType(selectedVehicle);
                serviceOrder.setLicensePlate(serviceOrderFormDTO.getLicensePlate());
                serviceOrder.setService(selectedServiceItem);
                serviceOrder.setServiceName(selectedServiceItem.getServiceName()); // Mengambil dari DTO
                serviceOrder.setFinalPrice(finalPrice); // Menggunakan harga yang dihitung di backend
                serviceOrder.setOrderNotes(serviceOrderFormDTO.getOrderNotes());
                serviceOrder.setSelectedDurationDays(serviceOrderFormDTO.getSelectedDurationDays()); // Menggunakan field dari DTO

                // Handle creation vs. update
                if (serviceOrder.getId() == null) {
                    serviceOrder.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    serviceOrder.setOrderStatus(ServiceOrder.OrderStatus.PENDING); // Set status awal
                    serviceOrderService.createServiceOrder(serviceOrder);
                    redirectAttributes.addFlashAttribute("successMessage", "Order Servis berhasil dibuat!");
                } else {
                    // Ketika update, pastikan field-field yang tidak ada di DTO (misal created_at)
                    // dipertahankan dari objek asli sebelum update.
                    // Anda mungkin perlu mengambil ServiceOrder asli terlebih dahulu.
                    ServiceOrder existingOrder = serviceOrderService.getServiceOrderById(serviceOrder.getId())
                                                                  .orElseThrow(() -> new IllegalArgumentException("Order Servis tidak ditemukan untuk update."));
                    serviceOrder.setCreatedAt(existingOrder.getCreatedAt()); // Pertahankan created_at
                    serviceOrder.setOrderStatus(existingOrder.getOrderStatus()); // Pertahankan status jika tidak diubah di form
                    serviceOrder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    serviceOrderService.updateServiceOrder(serviceOrder.getId(), serviceOrder);
                    redirectAttributes.addFlashAttribute("successMessage", "Order Servis berhasil diupdate!");
                }


                return "redirect:/admin/service-orders"; // Redirect ke halaman daftar admin
            } else {
                // Jika kendaraan atau layanan tidak ditemukan (meskipun harusnya sudah divalidasi oleh @NotNull di DTO)
                // Ini adalah fallback untuk kasus data tidak konsisten.
                redirectAttributes.addFlashAttribute("errorMessage", "Error: Kendaraan atau Layanan tidak ditemukan. Mohon coba lagi.");
                return "redirect:/admin/service-orders/new"; // Atau kembali ke form dengan data sebelumnya
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kesalahan data: " + e.getMessage());
            // Jika ada error, kembali ke form dengan data yang sudah diisi
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("vehicleTypes", vehicleService.getAllVehicles());
            model.addAttribute("serviceItems", serviceItemService.getAllServiceItems());
            return "admin/EditServiceOrderForm"; // Kembali ke form dengan pesan error
        } catch (Exception e) {
            e.printStackTrace(); // Penting untuk debugging
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan server: " + e.getMessage());
            // Redirect ke halaman daftar admin atau error page
            return "redirect:/admin/service-orders";
        }
    }

    // Endpoint untuk menampilkan form edit Service Order (Admin)
    @GetMapping("/service-orders/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ServiceOrder serviceOrder = serviceOrderService.getServiceOrderById(id)
                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service Order not found"));

        // Konversi ServiceOrder entity ke AdminServiceOrderFormDTO
        AdminServiceOrderFormDTO dto = convertServiceOrderToAdminDto(serviceOrder); // Implementasi ini

        model.addAttribute("serviceOrderFormDTO", dto);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("vehicleTypes", vehicleService.getAllVehicles());
        model.addAttribute("serviceItems", serviceItemService.getAllServiceItems());
        return "admin/EditServiceOrderForm";
    }

    // Metode konversi dari ServiceOrder ke AdminServiceOrderFormDTO
    private AdminServiceOrderFormDTO convertServiceOrderToAdminDto(ServiceOrder serviceOrder) {
        AdminServiceOrderFormDTO dto = new AdminServiceOrderFormDTO();
        dto.setId(serviceOrder.getId());
        dto.setUserId(serviceOrder.getUser() != null ? serviceOrder.getUser().getId() : null);
        dto.setCustomerName(serviceOrder.getCustomerName());
        dto.setCustomerContact(serviceOrder.getCustomerContact());
        dto.setCustomerAddress(serviceOrder.getCustomerAddress());
        dto.setVehicleModelName(serviceOrder.getVehicleModelName());
        dto.setVehicleTypeId(serviceOrder.getVehicleType() != null ? serviceOrder.getVehicleType().getId() : null);
        dto.setLicensePlate(serviceOrder.getLicensePlate());
        dto.setServiceId(serviceOrder.getService() != null ? serviceOrder.getService().getId() : null);
        dto.setServiceName(serviceOrder.getServiceName()); // Ambil serviceName dari entity
        dto.setSelectedDurationDays(serviceOrder.getSelectedDurationDays()); // Ambil selectedDurationDays dari entity
        dto.setFinalPrice(serviceOrder.getFinalPrice());
        dto.setOrderNotes(serviceOrder.getOrderNotes());
        return dto;
    }

    // Endpoint untuk menghapus Service Order (Admin)
    @PostMapping("/service-orders/delete/{id}")
    public String deleteServiceOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            serviceOrderService.deleteServiceOrder(id);
            redirectAttributes.addFlashAttribute("successMessage", "Order Servis berhasil dihapus!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error menghapus order servis: " + e.getMessage());
        }
        return "redirect:/admin/service-orders";
    }
    
    private final TransactionService transactionService;
    
    @GetMapping("/transaction")
    public String showTransactions(Model model) {
        List<Transaction> transactions = transactionService.getAllTransactions();
        model.addAttribute("transactions", transactions);
        return "admin/transaction"; // Use an admin-specific view path
    }

    @GetMapping("/transaction/edit/{id}")
    public String showEditTransactionForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Transaction> transaction = transactionService.getTransactionById(id);
        if (transaction.isPresent()) {
            model.addAttribute("transaction", transaction.get());
            model.addAttribute("statusOptions", Transaction.TransactionStatus.values());
            model.addAttribute("paymentMethodOptions", Transaction.PaymentMethod.values()); 
            return "admin/edit-transaction"; // Use an admin-specific view path
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Transaction not found!");
            return "redirect:/admin/transaction";
        }
    }

    @PostMapping("/transaction/update/{id}")
    public String updateTransaction(@PathVariable("id") Long id,
                                    @RequestParam("transactionStatus") String transactionStatusString,
                                    @RequestParam("paymentMethod") String paymentMethodString,
                                    RedirectAttributes redirectAttributes) {
        try {
            Transaction.TransactionStatus newStatus = Transaction.TransactionStatus.valueOf(transactionStatusString);
            Transaction.PaymentMethod newPaymentMethod = Transaction.PaymentMethod.valueOf(paymentMethodString);

            Transaction updatedTransaction = transactionService.updateTransactionStatusAndPaymentMethod(
                    id, newStatus, newPaymentMethod);

            if (updatedTransaction != null) {
                redirectAttributes.addFlashAttribute("successMessage", "Transaction updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Transaction not found for update!");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid transaction status or payment method provided! Error: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating transaction: " + e.getMessage());
        }
        return "redirect:/admin/transaction";
    }

    @GetMapping("/transactions/print/{serviceOrderId}")
    public String printReservationReceipt(@PathVariable Long serviceOrderId, Model model, RedirectAttributes redirectAttributes) {
        Optional<ServiceOrder> serviceOrderOptional = serviceOrderService.getServiceOrderById(serviceOrderId);

        if (serviceOrderOptional.isPresent()) {
            ServiceOrder serviceOrder = serviceOrderOptional.get();
            Optional<Transaction> transactionOptional = transactionService.getTransactionByServiceOrder(serviceOrder);

            if (transactionOptional.isPresent()) {
                Transaction transaction = transactionOptional.get();
                model.addAttribute("transaction", transaction);
                model.addAttribute("currentDate", new java.util.Date());
                return "customer/print_receipt"; // Use an admin-specific print view
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Transaction for this order not found.");
                return "redirect:/admin/transaction";
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Order not found.");
            return "redirect:/admin/transaction";
        }
    }

    @GetMapping("/specialize")
    public String listSpecializations(Model model) {
        List<SpecializationListDTO> specializations = specializationService.getAllSpecializationsWithCount();
        model.addAttribute("specializations", specializations);
        return "admin/Specialization";
    }

    // Menampilkan form untuk menambah spesialisasi baru
    @GetMapping("/specialize/new")
    public String showCreateSpecializationForm(Model model) {
        model.addAttribute("specialization", new Specialization()); // Tetap gunakan entitas Specialization untuk form input
        model.addAttribute("pageTitle", "Tambah Spesialisasi Baru");
        return "admin/Specializations_form";
    }

    // Menyimpan spesialisasi baru (atau update jika ada ID)
    @PostMapping("/specialize/save")
    public String saveSpecialization(@ModelAttribute("specialization") Specialization specialization,
                                     RedirectAttributes redirectAttributes) {
        specializationService.saveSpecialization(specialization);
        redirectAttributes.addFlashAttribute("message", "Spesialisasi berhasil disimpan!");
        return "redirect:/admin/specialize";
    }

    // Menampilkan form untuk mengedit spesialisasi
    @GetMapping("/specialize/edit/{id}")
    public String showEditSpecializationForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        // Ambil entitas Specialization asli untuk mengisi form edit
        return specializationService.getSpecializationById(id) // <<< MENGGUNAKAN METODE INI
                .map(specialization -> {
                    model.addAttribute("specialization", specialization); // Add the entity to the model
                    model.addAttribute("pageTitle", "Edit Spesialisasi");
                    return "admin/Specializations_form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Spesialisasi tidak ditemukan!");
                    return "redirect:/admin/specialize";
                });
    }

    // Memperbarui spesialisasi yang sudah ada
    @PostMapping("/specialize/update/{id}")
    public String updateSpecialization(@PathVariable("id") Long id,
                                       @ModelAttribute("specialization") Specialization specialization,
                                       RedirectAttributes redirectAttributes) {
        try {
            specializationService.updateSpecialization(id, specialization);
            redirectAttributes.addFlashAttribute("message", "Spesialisasi berhasil diperbarui!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal memperbarui spesialisasi: " + e.getMessage());
        }
        return "redirect:/admin/specialize";
    }

    // Menghapus spesialisasi
    @PostMapping("/specialize/delete/{id}")
    public String deleteSpecialization(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            specializationService.deleteSpecialization(id);
            redirectAttributes.addFlashAttribute("message", "Spesialisasi berhasil dihapus!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage()); // Pesan jika masih ada teknisi
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal menghapus spesialisasi: " + e.getMessage());
        }
        return "redirect:/admin/specialize";
    }

     @GetMapping("/services")
    public String listServiceItems(Model model) {
        List<ServiceItem> serviceItems = serviceItemService.getAllServiceItems();
        model.addAttribute("serviceItems", serviceItems);
        return "admin/ServicesList";
    }

    // --- Tambah Layanan ---
    // Endpoint baru untuk menampilkan form tambah Generalservice
    @GetMapping("/services/new/general")
    public String showCreateGeneralServiceForm(Model model) {
        model.addAttribute("serviceItem", new Generalservice());
        model.addAttribute("specializations", serviceItemService.getAllSpecializations());
        model.addAttribute("pageTitle", "Tambah Layanan Umum Baru");
        model.addAttribute("serviceType", "General"); // Indikator untuk form
        return "admin/services_form";
    }

    // Endpoint baru untuk menampilkan form tambah SpecializedService
    @GetMapping("/services/new/specialized")
    public String showCreateSpecializedServiceForm(Model model) {
        model.addAttribute("serviceItem", new SpecializedService());
        model.addAttribute("specializations", serviceItemService.getAllSpecializations());
        model.addAttribute("pageTitle", "Tambah Layanan Spesialis Baru");
        model.addAttribute("serviceType", "Spesialis"); // Indikator untuk form
        return "admin/Services_form";
    }

    // Endpoint yang menangani penyimpanan layanan (baik General maupun Spesialis)
    // Sekarang, kita akan menggunakan objek @ModelAttribute yang sudah merupakan instance subclass
    @PostMapping("/services/save/general")
    public String saveGeneralService(@ModelAttribute("serviceItem") Generalservice generalservice,
                                     RedirectAttributes redirectAttributes) {
        serviceItemService.saveServiceItem(generalservice);
        redirectAttributes.addFlashAttribute("message", "Layanan Umum berhasil disimpan!");
        return "redirect:/admin/services";
    }

    @PostMapping("/services/save/specialized")
    public String saveSpecializedService(@ModelAttribute("serviceItem") SpecializedService specializedService,
                                         RedirectAttributes redirectAttributes) {
        serviceItemService.saveServiceItem(specializedService);
        redirectAttributes.addFlashAttribute("message", "Layanan Spesialis berhasil disimpan!");
        return "redirect:/admin/services";
    }


    // --- Edit Layanan ---
    @GetMapping("/services/edit/{id}")
    public String showEditServiceItemForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return serviceItemService.getServiceItemById(id)
                .map(serviceItem -> {
                    model.addAttribute("serviceItem", serviceItem);
                    model.addAttribute("specializations", serviceItemService.getAllSpecializations());
                    model.addAttribute("pageTitle", "Edit Layanan");
                    model.addAttribute("serviceType", serviceItem.getServiceType()); // Gunakan tipe yang sudah ada
                    return "admin/Services_form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Layanan tidak ditemukan!");
                    return "redirect:/admin/services";
                });
    }

    // Endpoint yang menangani pembaruan layanan (baik General maupun Spesialis)
    // Kita perlu memeriksa jenis instance dan memperbarui properti yang relevan.
    @PostMapping("/services/update/{id}")
    public String updateServiceItem(@PathVariable("id") Long id,
                                    @ModelAttribute("serviceItem") ServiceItem serviceItem, // Menerima ServiceItem generik
                                    RedirectAttributes redirectAttributes) {
        try {
            ServiceItem existingServiceItem = serviceItemService.getServiceItemById(id)
                                                    .orElseThrow(() -> new RuntimeException("Layanan tidak ditemukan dengan ID: " + id));

            // Penting: Dengan SINGLE_TABLE inheritance, mengubah tipe layanan (misalnya dari General ke Spesialis)
            // setelah dibuat TIDAK didukung langsung oleh JPA melalui update.
            // Jika Anda ingin mengubah jenis layanan, Anda harus menghapus yang lama dan membuat yang baru.
            // Untuk saat ini, kita akan mengasumsikan jenis layanan TIDAK berubah saat mengedit.
            // Kita hanya akan memperbarui properti yang relevan berdasarkan jenis yang sudah ada.

            // Salin properti umum
            existingServiceItem.setServiceName(serviceItem.getServiceName());
            existingServiceItem.setServiceCategory(serviceItem.getServiceCategory());
            existingServiceItem.setBasePrice(serviceItem.getBasePrice());
            existingServiceItem.setRequiredSpecialization(serviceItem.getRequiredSpecialization());

            // Salin properti spesifik subclass
            if (existingServiceItem instanceof Generalservice && serviceItem instanceof Generalservice) {
                Generalservice existingGeneral = (Generalservice) existingServiceItem;
                Generalservice incomingGeneral = (Generalservice) serviceItem;
                existingGeneral.setGeneralDurationDaysMin(incomingGeneral.getGeneralDurationDaysMin());
                existingGeneral.setGeneralDurationDaysMax(incomingGeneral.getGeneralDurationDaysMax());
                // Pastikan special duration diset 0
                existingGeneral.setSpecialDurationDaysMin(0);
                existingGeneral.setSpecialDurationDaysMax(0);
            } else if (existingServiceItem instanceof SpecializedService && serviceItem instanceof SpecializedService) {
                SpecializedService existingSpecialized = (SpecializedService) existingServiceItem;
                SpecializedService incomingSpecialized = (SpecializedService) serviceItem;
                existingSpecialized.setSpecialDurationDaysMin(incomingSpecialized.getSpecialDurationDaysMin());
                existingSpecialized.setSpecialDurationDaysMax(incomingSpecialized.getSpecialDurationDaysMax());
                // Pastikan general duration diset 0
                existingSpecialized.setGeneralDurationDaysMin(0);
                existingSpecialized.setGeneralDurationDaysMax(0);
            } else {
                // Ini akan terjadi jika serviceType diubah di form dan dikirim sebagai subclass yang berbeda,
                // atau jika ada kesalahan dalam transmisi data.
                // Anda mungkin ingin melempar exception atau memberikan pesan error.
                throw new RuntimeException("Tipe layanan tidak dapat diubah setelah dibuat, atau tipe tidak cocok.");
            }

            serviceItemService.saveServiceItem(existingServiceItem);
            redirectAttributes.addFlashAttribute("message", "Layanan berhasil diperbarui!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal memperbarui layanan: " + e.getMessage());
        }
        return "redirect:/admin/services";
    }

    @PostMapping("/services/delete/{id}")
    public String deleteServiceItem(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            serviceItemService.deleteServiceItem(id);
            redirectAttributes.addFlashAttribute("message", "Layanan berhasil dihapus!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal menghapus layanan: " + e.getMessage());
        }
        return "redirect:/admin/services";
    }

    @GetMapping("/vehicles")
    public String listVehicles(Model model) {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        model.addAttribute("vehicles", vehicles);
        return "admin/VehiclesList"; // Path template Thymeleaf
    }

    // Menampilkan form untuk menambah Vehicle baru
    @GetMapping("/vehicles/new")
    public String showCreateVehicleForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("pageTitle", "Tambah Tipe Kendaraan Baru");
        return "admin/Vehicles_form"; // Path template Thymeleaf
    }

    // Menangani penyimpanan Vehicle baru
    @PostMapping("/vehicles/save")
    public String saveVehicle(@ModelAttribute("vehicle") Vehicle vehicle,
                              RedirectAttributes redirectAttributes) {
        try {
            vehicleService.saveVehicle(vehicle);
            redirectAttributes.addFlashAttribute("message", "Tipe kendaraan berhasil disimpan!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal menyimpan tipe kendaraan: " + e.getMessage());
        }
        return "redirect:/admin/vehicles";
    }

    // Menampilkan form untuk mengedit Vehicle
    @GetMapping("/vehicles/edit/{id}")
    public String showEditVehicleForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) { // ID Integer
        return vehicleService.getVehicleById(id)
                .map(vehicle -> {
                    model.addAttribute("vehicle", vehicle);
                    model.addAttribute("pageTitle", "Edit Tipe Kendaraan");
                    return "admin/Vehicles_form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Tipe kendaraan tidak ditemukan!");
                    return "redirect:/admin/vehicles";
                });
    }

    // Menangani pembaruan Vehicle
    @PostMapping("/vehicles/update/{id}")
    public String updateVehicle(@PathVariable("id") Integer id, // ID Integer
                                @ModelAttribute("vehicle") Vehicle vehicle,
                                RedirectAttributes redirectAttributes) {
        try {
            vehicleService.updateVehicle(id, vehicle);
            redirectAttributes.addFlashAttribute("message", "Tipe kendaraan berhasil diperbarui!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal memperbarui tipe kendaraan: " + e.getMessage());
        }
        return "redirect:/admin/vehicles";
    }

    // Menghapus Vehicle
    @PostMapping("/vehicles/delete/{id}")
    public String deleteVehicle(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) { // ID Integer
        try {
            vehicleService.deleteVehicle(id);
            redirectAttributes.addFlashAttribute("message", "Tipe kendaraan berhasil dihapus!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal menghapus tipe kendaraan: " + e.getMessage());
        }
        return "redirect:/admin/vehicles";
    }

}