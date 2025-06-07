package pbo.autocare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import pbo.autocare.model.Customer; // Import ini
import pbo.autocare.service.UserService; // Import ini

@Controller
public class AuthController {

    private final UserService userService; // Injeksi interface UserService

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("customer", new Customer()); // Objek kosong untuk form binding
        return "register";
    }

    @PostMapping("/register")
    public String registerCustomer(
            @Valid @ModelAttribute("customer") Customer customer, // Tambahkan @Valid
            BindingResult bindingResult, // Tambahkan BindingResult
            Model model) {

        // 1. Cek Validasi Form (annotations di model)
        if (bindingResult.hasErrors()) {
            // Jika ada error validasi, Spring akan secara otomatis menambahkannya ke model.
            // Cukup kembalikan ke halaman registrasi agar error ditampilkan.
            return "register";
        }

        try {
            // 2. Validasi Kustom: Cek apakah username sudah ada
            if (userService.findUserByUsername(customer.getUsername()).isPresent()) {
                model.addAttribute("error", "Username ini sudah terdaftar. Mohon gunakan username lain.");
                return "register";
            }

            // Jika semua validasi sukses, simpan customer
            userService.registerNewCustomer(customer);
            return "redirect:/login?registered"; // Redirect ke halaman login dengan pesan sukses
        } catch (Exception e) {
            // 3. Tangani kesalahan tak terduga saat menyimpan ke database
            model.addAttribute("error", "Registrasi gagal karena kesalahan sistem. Silakan coba lagi.");
            //e.printStackTrace(); // SANGAT PENTING: Cetak stack trace ke konsol untuk debugging!
            return "register"; // Kembali ke halaman registrasi dengan pesan error
        }
    }
}