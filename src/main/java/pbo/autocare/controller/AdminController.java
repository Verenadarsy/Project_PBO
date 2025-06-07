package pbo.autocare.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import pbo.autocare.model.Customer; // Import Customer
import pbo.autocare.model.User;    // Import User
import pbo.autocare.service.CustomerService;
import pbo.autocare.service.UserServiceImpl; // Menggunakan UserServiceImpl

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserServiceImpl userService;

    @Autowired
    private CustomerService customerService;

    public AdminController(UserServiceImpl userService) { // Constructor injection
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

    // --- METODE INI ADALAH SATU-SATUNYA UNTUK MENAMPILKAN DAFTAR PELANGGAN ---
    // PASTIKAN ANDA MENGHAPUS METHOD LAIN DENGAN @GetMapping("/customers") DARI FILE INI
    @GetMapping("/customers")
    public String listCustomers(Model model) {
        List<Customer> customers = userService.getAllCustomers(); // Memanggil metode getAllCustomers dari UserServiceImpl
        model.addAttribute("customers", customers); // Menambahkan daftar pelanggan ke model

        // Atribut untuk penyesuaian tampilan HTML (seperti judul halaman, judul header, link kembali)
        model.addAttribute("pageTitle", "Daftar Pelanggan (Admin)");
        model.addAttribute("headerTitle", "Daftar Pelanggan (Admin)");
        model.addAttribute("backLink", "/admin/dashboard");
        model.addAttribute("backText", "Kembali ke Dashboard Admin");

        return "admin/customer_list"; // Merujuk ke templates/admin/customer_list.html
    }
    // --- AKHIR DARI METODE LIST CUSTOMERS YANG BENAR ---
// CREATE: Menampilkan form tambah pelanggan baru
    @GetMapping("/customers/new")
    public String showAddCustomerForm(Model model) {
        model.addAttribute("customerFormDTO", new CustomerFormDTO());
        return "admin/EditCustomerForm"; // Menggunakan form yang sama (customer_form.html)
    }

    // UPDATE: Menampilkan form edit pelanggan
    @GetMapping("/customers/edit/{id}") // URL untuk form edit, misal /admin/customers/edit/1
    public String showEditCustomerForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Customer> customer = customerService.findCustomerById(id);
        if (customer.isPresent()) {
            // Mapping Entity ke DTO untuk mengisi form
            CustomerFormDTO customerFormDTO = new CustomerFormDTO(
                customer.get().getId(),
                customer.get().getUsername(),
                null, // Jangan kirim password asli ke form untuk keamanan
                customer.get().getEmail(),
                customer.get().getFullName(),
                customer.get().getPhoneNumber()
            );
            model.addAttribute("customerFormDTO", customerFormDTO);
            return "admin/EditCustomerForm"; // Menggunakan form yang sama (customer_form.html)
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Pelanggan tidak ditemukan untuk diedit.");
            return "redirect:/admin/customers"; // Redirect kembali ke daftar jika tidak ditemukan
        }
    }

    // CREATE / UPDATE: Menyimpan pelanggan (baru atau diedit)
    // Metode ini menangani kedua kasus: tambah baru (id == null) dan edit (id != null)
    @PostMapping("/customers/save")
    public String saveCustomer(@ModelAttribute("customerFormDTO") @Valid CustomerFormDTO customerFormDTO,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {

        // Tambahan validasi manual password saat create
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


    // DELETE: Menghapus pelanggan
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
        return "technician_list"; // Merujuk ke templates/technician_list.html
    }

    @GetMapping("/staff")
    public String listStaff(Model model) {
        List<User> staffList = userService.getAllStaff();
        model.addAttribute("staffs", staffList);
        model.addAttribute("pageTitle", "Daftar Staff (Admin)");
        model.addAttribute("headerTitle", "Daftar Staff (Admin)");
        model.addAttribute("backLink", "/admin/dashboard");
        model.addAttribute("backText", "Kembali ke Dashboard Admin");
        return "staff_list"; // Merujuk ke templates/staff_list.html
    }

    @GetMapping("/newstaff")
    public String registerstaff() {
        return "register_staff";
    }
    
    @GetMapping("/serviceorders")
    public String managementService() {
        return "service_order_detail";
    }
    
    @GetMapping("/transactions")
    public String manageTransactions() {
        return "admin_transactions";
    }

    @PostMapping("/transactions/add")
    public String addTransaction() {
        return "redirect:/admin/transactions";
    }

    @PostMapping("/transactions/edit")
    public String editTransaction() {
        return "redirect:/admin/transactions";
    }

    @PostMapping("/transactions/delete")
    public String deleteTransaction() {
        return "redirect:/admin/transactions";
    }
}