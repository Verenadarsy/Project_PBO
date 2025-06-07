package pbo.autocare.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Pastikan ini di-import
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // Tetap sertakan jika ada @PostMapping
import org.springframework.web.bind.annotation.RequestMapping;

import pbo.autocare.model.Customer; // Import Customer
import pbo.autocare.model.User;    // Import User
import pbo.autocare.service.UserServiceImpl; // Menggunakan UserServiceImpl

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserServiceImpl userService;

    public AdminController(UserServiceImpl userService) { // Constructor injection
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard() {
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