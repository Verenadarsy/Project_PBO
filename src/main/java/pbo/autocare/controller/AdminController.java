package pbo.autocare.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Pastikan ini di-import
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // Tetap sertakan jika ada @PostMapping
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import pbo.autocare.dto.CustomerFormDTO;
import pbo.autocare.dto.TechnicianFormDTO;
import pbo.autocare.model.Customer;
// import pbo.autocare.model.ServiceOrder;
import pbo.autocare.model.Specialization;
import pbo.autocare.model.Staff;
import pbo.autocare.model.Technician;
import pbo.autocare.model.User; 
import pbo.autocare.repository.SpecializationRepository;
import pbo.autocare.repository.UserRepository;
import pbo.autocare.service.CustomerService;
// import pbo.autocare.service.ServiceItemService;
// import pbo.autocare.service.ServiceOrderService;
import pbo.autocare.service.UserServiceImpl;
// import pbo.autocare.service.VehicleService; 

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserServiceImpl userService;

    @Autowired
    private CustomerService customerService;

    public AdminController(UserServiceImpl userService) { 
        this.userService = userService;
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

    // @Autowired
    // private ServiceOrderService serviceOrderService;

    // @Autowired
    // private VehicleService vehicleService;
    
    // @Autowired
    // private ServiceItemService serviceItemService;

    @GetMapping("/service-orders")
    public String managementService() {
        return "ServiceOrderList"; // Ganti dengan nama template yang sesuaiq
    }
    
    @GetMapping("/transactions")
    public String manageTransactions() {
        return "admin_transactions";
    }

    // // Show form for creating a new service order
    // // @GetMapping("/service-orders/new")
    // // public String showCreateForm(Model model) {
    // //         model.addAttribute("serviceOrder", new ServiceOrder());
    // //     // Add lists of existing Users, Vehicles, and ServiceItems to populate dropdowns
    // //         model.addAttribute("users", userService.getAllCustomers());
    // //         model.addAttribute("vehicleTypes", vehicleService.getAllVehicles());
    // //         model.addAttribute("serviceItems", serviceItemService.getAllServiceItems());
    // //     return "ServiceOrderForm"; // Corresponds to src/main/resources/templates/service-orders/form.html
    // // }

    // // Handle creation of a new service order
    // @PostMapping("/save")
    // public String saveServiceOrder(@ModelAttribute("serviceOrder") ServiceOrder serviceOrder) {
    //     // Important: You'll need to set the related User, Vehicle, and ServiceItem objects
    //     // based on the IDs submitted from the form. This usually involves fetching them
    //     // from their respective services.
    //     // Example:
    //     // User user = userService.getUserById(serviceOrder.getUser().getId()).orElseThrow(...);
    //     // serviceOrder.setUser(user);

    //     serviceOrderService.createServiceOrder(serviceOrder);
    //     return "redirect:/service-orders"; // Redirect to the list page
    // }

    // // Show form for editing an existing service order
    // @GetMapping("/edit/{id}")
    // public String showEditForm(@PathVariable Long id, Model model) {
    //     Optional<ServiceOrder> serviceOrder = serviceOrderService.getServiceOrderById(id);
    //     if (serviceOrder.isPresent()) {
    //         model.addAttribute("serviceOrder", serviceOrder.get());
    //         // Add lists of existing Users, Vehicles, and ServiceItems to populate dropdowns
    //         // model.addAttribute("users", userService.getAllUsers());
    //         // model.addAttribute("vehicleTypes", vehicleService.getAllVehicles());
    //         // model.addAttribute("serviceItems", serviceItemService.getAllServiceItems());
    //         return "service-orders/form";
    //     }
    //     return "redirect:/service-orders"; // Not found, redirect to list
    // }

    // // Handle updating an existing service order
    // // @PostMapping("/update/{id}") // or you can use a single /save and check for ID
    // // public String updateServiceOrder(@PathVariable Long id, @ModelAttribute("serviceOrder") ServiceOrder serviceOrder) {
    // //     serviceOrderService.updateServiceOrder(id, serviceOrder);
    // //     return "redirect:/service-orders";
    // // }

    // // Handle deletion of a service order
    // @GetMapping("/delete/{id}") // Often done with POST/DELETE requests in REST, but GET for simplicity in web
    // public String deleteServiceOrder(@PathVariable Long id) {
    //     serviceOrderService.deleteServiceOrder(id);
    //     return "redirect:/service-orders";
    // }
}