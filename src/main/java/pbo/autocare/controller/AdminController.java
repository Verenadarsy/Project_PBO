package pbo.autocare.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; 
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; 
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

    @GetMapping("/technician/new")
    public String showAddTechnicianForm(Model model) {
        if (!model.containsAttribute("technicianFormDTO")) {
             model.addAttribute("technicianFormDTO", new TechnicianFormDTO());
        }
        model.addAttribute("specializations", specializationRepository.findAll());
        return "admin/EditTechForm"; 
    }

    @PostMapping("/technician/save")
    public String saveTechnician(@ModelAttribute("technicianFormDTO") @Valid TechnicianFormDTO technicianFormDTO,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            System.out.println("--- INITIAL @VALID DTO ERRORS ---");
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
            bindingResult.getFieldErrors().forEach(error -> System.out.println("Field: " + error.getField() + ", Error: " + error.getDefaultMessage()));
            System.out.println("--- END INITIAL @VALID DTO ERRORS ---");
        }

        Technician currentTechnicianInDb = null;
        if (technicianFormDTO.getId() != null) {
            Optional<Technician> existingTechnicianOpt = userRepository.findTechnicianById(technicianFormDTO.getId());
            if (existingTechnicianOpt.isPresent()) {
                currentTechnicianInDb = existingTechnicianOpt.get();
            } else {
                bindingResult.rejectValue("id", "NotFound", "Teknisi dengan ID ini tidak ditemukan.");
            }
        }

        if (technicianFormDTO.getId() == null) {
            if (technicianFormDTO.getPassword() == null || technicianFormDTO.getPassword().isEmpty()) {
                bindingResult.rejectValue("password", "NotBlank", "Password tidak boleh kosong untuk akun baru.");
            }
        }

        if (userRepository.findByUsername(technicianFormDTO.getUsername()).isPresent()) {
            User userWithSameUsername = userRepository.findByUsername(technicianFormDTO.getUsername()).get();
            if (technicianFormDTO.getId() == null || !userWithSameUsername.getId().equals(technicianFormDTO.getId())) {

                bindingResult.rejectValue("username", "Duplicate", "Username sudah terdaftar.");
            }
        }

        if (userRepository.findByEmail(technicianFormDTO.getEmail()).isPresent()) {
            User userWithSameEmail = userRepository.findByEmail(technicianFormDTO.getEmail()).get();
            if (technicianFormDTO.getId() == null || !userWithSameEmail.getId().equals(technicianFormDTO.getId())) {

                bindingResult.rejectValue("email", "Duplicate", "Email sudah terdaftar.");
            }
        }

        if (bindingResult.hasErrors()) {
            System.out.println("--- FINAL VALIDATION ERRORS (all checks) ---");
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
            bindingResult.getFieldErrors().forEach(error -> System.out.println("Field: " + error.getField() + ", Error: " + error.getDefaultMessage()));
            System.out.println("--- END FINAL VALIDATION ERRORS ---");

            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.technicianFormDTO", bindingResult);
            redirectAttributes.addFlashAttribute("technicianFormDTO", technicianFormDTO);
            redirectAttributes.addFlashAttribute("errorMessage", "Data teknisi tidak valid. Mohon periksa kembali input Anda.");
            redirectAttributes.addFlashAttribute("specializations", specializationRepository.findAll());

            String redirectToUrl = (technicianFormDTO.getId() == null) ? "/admin/technician/new" : "/admin/technician/edit/" + technicianFormDTO.getId();
            return "redirect:" + redirectToUrl;
        }

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

        if (technicianFormDTO.getId() == null) { 
            technicianToSave = new Technician();
            technicianToSave.setUsername(technicianFormDTO.getUsername());
            technicianToSave.setPassword(passwordEncoder.encode(technicianFormDTO.getPassword()));
            redirectAttributes.addFlashAttribute("successMessage", "Teknisi berhasil ditambahkan!");
        } else { 
            if (currentTechnicianInDb == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Teknisi tidak ditemukan untuk diedit.");
                return "redirect:/admin/technician";
            }
            technicianToSave = currentTechnicianInDb;

            technicianToSave.setUsername(technicianFormDTO.getUsername());

            if (technicianFormDTO.getPassword() != null && !technicianFormDTO.getPassword().isEmpty()) {
                technicianToSave.setPassword(passwordEncoder.encode(technicianFormDTO.getPassword()));
            }
     
            technicianToSave.setEmail(technicianFormDTO.getEmail());
            technicianToSave.setFullName(technicianFormDTO.getFullName());
            technicianToSave.setPhoneNumber(technicianFormDTO.getPhoneNumber());


            redirectAttributes.addFlashAttribute("successMessage", "Teknisi berhasil diperbarui!");
        }

        technicianToSave.setSpecialization(specializationOptional.get());

        userRepository.save(technicianToSave);

        return "redirect:/admin/technician";

    }

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

            model.addAttribute("technicianFormDTO", dto);
            model.addAttribute("specializations", specializationRepository.findAll());

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

        userService.saveStaff(staff);
        redirectAttributes.addFlashAttribute("message", "Staff account saved successfully!");
        return "redirect:/admin/staff";
    }

    @GetMapping("/staff/edit/{id}")
    public String showEditStaffForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Staff> staff = userService.getStaffById(id);
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
        userService.deleteStaff(id);
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

    @GetMapping("/service-orders")
    public String listServiceOrders(Model model) {
        List<ServiceOrder> serviceOrders = serviceOrderRepository.findAllWithVehicleTypeAndService();
        model.addAttribute("orders", serviceOrders);
        return "admin/ServiceOrderList";
    }

    @GetMapping("/service-orders/new")
    public String showAdminCreateForm(Model model) {
        AdminServiceOrderFormDTO dto = new AdminServiceOrderFormDTO();
        model.addAttribute("serviceOrderFormDTO", dto);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("vehicleTypes", vehicleService.getAllVehicles());
        model.addAttribute("serviceItems", serviceItemService.getAllServiceItems());
        return "admin/EditServiceOrderForm";
    }

    @PostMapping("/service-orders/save")
    public String saveServiceOrder(@ModelAttribute("serviceOrderFormDTO") @Valid AdminServiceOrderFormDTO serviceOrderFormDTO,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("vehicleTypes", vehicleService.getAllVehicles());
            model.addAttribute("serviceItems", serviceItemService.getAllServiceItems());
            return "admin/EditServiceOrderForm";
        }

        try {

            User selectedUser = userService.getCustomerById(serviceOrderFormDTO.getUserId())
                                        .orElseThrow(() -> new IllegalArgumentException("User dengan ID " + serviceOrderFormDTO.getUserId() + " tidak ditemukan."));

            Optional<Vehicle> vehicleOptional = vehicleService.getVehicleById(serviceOrderFormDTO.getVehicleTypeId());
            Optional<ServiceItem> serviceOptional = serviceItemService.getServiceItemById(serviceOrderFormDTO.getServiceId());

            if (vehicleOptional.isPresent() && serviceOptional.isPresent()) {
                ServiceItem selectedServiceItem = serviceOptional.get();
                Vehicle selectedVehicle = vehicleOptional.get();

                BigDecimal finalPrice = selectedServiceItem.calculateFinalPrice(selectedVehicle);
                serviceOrderFormDTO.setFinalPrice(finalPrice);


                ServiceOrder serviceOrder = new ServiceOrder();

                if (serviceOrderFormDTO.getId() != null) {
                    serviceOrder.setId(serviceOrderFormDTO.getId());
                }

                serviceOrder.setUser(selectedUser); 
                serviceOrder.setCustomerName(serviceOrderFormDTO.getCustomerName());
                serviceOrder.setCustomerContact(serviceOrderFormDTO.getCustomerContact());
                serviceOrder.setCustomerAddress(serviceOrderFormDTO.getCustomerAddress());
                serviceOrder.setVehicleModelName(serviceOrderFormDTO.getVehicleModelName());
                serviceOrder.setVehicleType(selectedVehicle);
                serviceOrder.setLicensePlate(serviceOrderFormDTO.getLicensePlate());
                serviceOrder.setService(selectedServiceItem);
                serviceOrder.setServiceName(selectedServiceItem.getServiceName());
                serviceOrder.setFinalPrice(finalPrice);
                serviceOrder.setOrderNotes(serviceOrderFormDTO.getOrderNotes());
                serviceOrder.setSelectedDurationDays(serviceOrderFormDTO.getSelectedDurationDays());

                if (serviceOrder.getId() == null) {
                    serviceOrder.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    serviceOrder.setOrderStatus(ServiceOrder.OrderStatus.PENDING); 
                    serviceOrderService.createServiceOrder(serviceOrder);
                    redirectAttributes.addFlashAttribute("successMessage", "Order Servis berhasil dibuat!");
                } else {
                    
                    ServiceOrder existingOrder = serviceOrderService.getServiceOrderById(serviceOrder.getId())
                                                                  .orElseThrow(() -> new IllegalArgumentException("Order Servis tidak ditemukan untuk update."));
                    serviceOrder.setCreatedAt(existingOrder.getCreatedAt());
                    serviceOrder.setOrderStatus(existingOrder.getOrderStatus());
                    serviceOrder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    serviceOrderService.updateServiceOrder(serviceOrder.getId(), serviceOrder);
                    redirectAttributes.addFlashAttribute("successMessage", "Order Servis berhasil diupdate!");
                }


                return "redirect:/admin/service-orders";
            } else {

                redirectAttributes.addFlashAttribute("errorMessage", "Error: Kendaraan atau Layanan tidak ditemukan. Mohon coba lagi.");
                return "redirect:/admin/service-orders/new"; 
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kesalahan data: " + e.getMessage());

            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("vehicleTypes", vehicleService.getAllVehicles());
            model.addAttribute("serviceItems", serviceItemService.getAllServiceItems());
            return "admin/EditServiceOrderForm"; 
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan server: " + e.getMessage());
  
            return "redirect:/admin/service-orders";
        }
    }

    @GetMapping("/service-orders/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ServiceOrder serviceOrder = serviceOrderService.getServiceOrderById(id)
                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service Order not found"));

        AdminServiceOrderFormDTO dto = convertServiceOrderToAdminDto(serviceOrder);

        model.addAttribute("serviceOrderFormDTO", dto);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("vehicleTypes", vehicleService.getAllVehicles());
        model.addAttribute("serviceItems", serviceItemService.getAllServiceItems());
        return "admin/EditServiceOrderForm";
    }

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
        dto.setServiceName(serviceOrder.getServiceName()); 
        dto.setSelectedDurationDays(serviceOrder.getSelectedDurationDays());
        dto.setFinalPrice(serviceOrder.getFinalPrice());
        dto.setOrderNotes(serviceOrder.getOrderNotes());
        return dto;
    }

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
        return "admin/transaction";
    }

    @GetMapping("/transaction/edit/{id}")
    public String showEditTransactionForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Transaction> transaction = transactionService.getTransactionById(id);
        if (transaction.isPresent()) {
            model.addAttribute("transaction", transaction.get());
            model.addAttribute("statusOptions", Transaction.TransactionStatus.values());
            model.addAttribute("paymentMethodOptions", Transaction.PaymentMethod.values()); 
            return "admin/edit-transaction"; 
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
                return "customer/print_receipt"; 
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

    @GetMapping("/specialize/new")
    public String showCreateSpecializationForm(Model model) {
        model.addAttribute("specialization", new Specialization()); 
        model.addAttribute("pageTitle", "Tambah Spesialisasi Baru");
        return "admin/Specializations_form";
    }

    @PostMapping("/specialize/save")
    public String saveSpecialization(@ModelAttribute("specialization") Specialization specialization,
                                     RedirectAttributes redirectAttributes) {
        specializationService.saveSpecialization(specialization);
        redirectAttributes.addFlashAttribute("message", "Spesialisasi berhasil disimpan!");
        return "redirect:/admin/specialize";
    }

    @GetMapping("/specialize/edit/{id}")
    public String showEditSpecializationForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {

        return specializationService.getSpecializationById(id) 
                .map(specialization -> {
                    model.addAttribute("specialization", specialization);
                    model.addAttribute("pageTitle", "Edit Spesialisasi");
                    return "admin/Specializations_form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Spesialisasi tidak ditemukan!");
                    return "redirect:/admin/specialize";
                });
    }

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

    @PostMapping("/specialize/delete/{id}")
    public String deleteSpecialization(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            specializationService.deleteSpecialization(id);
            redirectAttributes.addFlashAttribute("message", "Spesialisasi berhasil dihapus!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage()); 
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

    @GetMapping("/services/new/general")
    public String showCreateGeneralServiceForm(Model model) {
        model.addAttribute("serviceItem", new Generalservice());
        model.addAttribute("specializations", serviceItemService.getAllSpecializations());
        model.addAttribute("pageTitle", "Tambah Layanan Umum Baru");
        model.addAttribute("serviceType", "General"); 
        return "admin/services_form";
    }

    @GetMapping("/services/new/specialized")
    public String showCreateSpecializedServiceForm(Model model) {
        model.addAttribute("serviceItem", new SpecializedService());
        model.addAttribute("specializations", serviceItemService.getAllSpecializations());
        model.addAttribute("pageTitle", "Tambah Layanan Spesialis Baru");
        model.addAttribute("serviceType", "Spesialis"); 
        return "admin/Services_form";
    }

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

    @GetMapping("/services/edit/{id}")
    public String showEditServiceItemForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return serviceItemService.getServiceItemById(id)
                .map(serviceItem -> {
                    model.addAttribute("serviceItem", serviceItem);
                    model.addAttribute("specializations", serviceItemService.getAllSpecializations());
                    model.addAttribute("pageTitle", "Edit Layanan");
                    model.addAttribute("serviceType", serviceItem.getServiceType()); 
                    return "admin/Services_form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Layanan tidak ditemukan!");
                    return "redirect:/admin/services";
                });
    }

    @PostMapping("/services/update/{id}")
    public String updateServiceItem(@PathVariable("id") Long id,
                                    @ModelAttribute("serviceItem") ServiceItem serviceItem,
                                    RedirectAttributes redirectAttributes) {
        try {
            ServiceItem existingServiceItem = serviceItemService.getServiceItemById(id)
                                                    .orElseThrow(() -> new RuntimeException("Layanan tidak ditemukan dengan ID: " + id));

            existingServiceItem.setServiceName(serviceItem.getServiceName());
            existingServiceItem.setServiceCategory(serviceItem.getServiceCategory());
            existingServiceItem.setBasePrice(serviceItem.getBasePrice());
            existingServiceItem.setRequiredSpecialization(serviceItem.getRequiredSpecialization());

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
        return "admin/VehiclesList"; 
    }

    @GetMapping("/vehicles/new")
    public String showCreateVehicleForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("pageTitle", "Tambah Tipe Kendaraan Baru");
        return "admin/Vehicles_form";
    }

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

    @GetMapping("/vehicles/edit/{id}")
    public String showEditVehicleForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
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

    @PostMapping("/vehicles/update/{id}")
    public String updateVehicle(@PathVariable("id") Integer id, 
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

    @PostMapping("/vehicles/delete/{id}")
    public String deleteVehicle(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) { 
        try {
            vehicleService.deleteVehicle(id);
            redirectAttributes.addFlashAttribute("message", "Tipe kendaraan berhasil dihapus!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal menghapus tipe kendaraan: " + e.getMessage());
        }
        return "redirect:/admin/vehicles";
    }

}