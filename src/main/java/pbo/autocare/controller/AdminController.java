package pbo.autocare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
// @Secured("ROLE_ADMIN") // Bisa pakai ini juga, tapi sudah diatur di SecurityConfig
public class AdminController {

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin_dashboard";
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
