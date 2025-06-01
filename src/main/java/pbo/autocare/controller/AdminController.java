package pbo.autocare.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pbo.autocare.model.User;
import pbo.autocare.service.UserService;
import org.springframework.ui.Model;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin_dashboard";
    }

    @GetMapping("/customers")
    public String listCustomer() {
        return "costumer_list";
    }

    @GetMapping("/technician")
    public String listTech(Model model) {
        List<User> techList = userService.getAllTechnicians();
        model.addAttribute("technicians", techList);
        return "technician_list";
    }

    @Autowired
    private UserService userService;

    @GetMapping("/staff")
    public String listStaff(Model model) {
        List<User> staffList = userService.getAllStaff();
        model.addAttribute("staffs", staffList);
        return "staff_list";
    }

    @GetMapping("/newstaff")
    public String registerstaff() {
        return "register_staff";
    }
    
    @GetMapping("/serviceorders")
    public String managementService() {
        return "service_order_detail";
    }
    

    // Tambahkan endpoint untuk kelola transaksi (CRUD)
    @GetMapping("/transactions")
    public String manageTransactions() {
        // Logika untuk menampilkan daftar transaksi
        return "admin_transactions"; // Contoh: admin_transactions.html
    }

    @PostMapping("/transactions/add")
    public String addTransaction() {
        // Logika untuk menambahkan transaksi baru
        return "redirect:/admin/transactions"; // Redirect ke daftar transaksi setelah penambahan
    }

    @PostMapping("/transactions/edit")
    public String editTransaction() {
        // Logika untuk mengedit transaksi yang sudah ada
        return "redirect:/admin/transactions"; // Redirect ke daftar transaksi setelah pengeditan
    }

    @PostMapping("/transactions/delete")
    public String deleteTransaction() {
        // Logika untuk menghapus transaksi
        return "redirect:/admin/transactions"; // Redirect ke daftar transaksi setelah penghapusan
    }
}
