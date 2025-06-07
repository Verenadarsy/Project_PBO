package pbo.autocare.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import pbo.autocare.model.Customer; // Import Customer
import pbo.autocare.model.User; // Import interface UserService
import pbo.autocare.service.UserService;

@Controller
@RequestMapping("/technician")
public class TechnicianController {

    private final UserService userService; // Gunakan interface UserService

    // Constructor injection
    public TechnicianController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String technicianDashboard() {
        return "technician/dashboard"; // Mengarahkan ke templates/technician/dashboard.html
    }

    @GetMapping("/list") // Endpoint untuk menampilkan daftar Teknisi
    public String showTechnicianList(Model model) {
        List<User> technicianList = userService.getAllTechnicians();
        model.addAttribute("technicians", technicianList);
        return "technician_list"; // Mengarahkan ke templates/technician/technician_list.html
    }

    // --- FITUR DAFTAR PELANGGAN UNTUK TEKNISI ---
    @GetMapping("/customers") // <--- Ini yang sekarang menangani URL /technician/customers
    public String listCustomers(Model model) {
        List<Customer> customers = userService.getAllCustomers();
        model.addAttribute("customers", customers);
        model.addAttribute("pageTitle", "Daftar Pelanggan (Teknisi)");
        model.addAttribute("headerTitle", "Daftar Pelanggan (Teknisi)");
        model.addAttribute("backLink", "/technician/dashboard");
        model.addAttribute("backText", "Kembali ke Dashboard Teknisi");
        return "technician/customer_list";
    }
}