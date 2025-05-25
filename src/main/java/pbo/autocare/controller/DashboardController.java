package pbo.autocare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController { // Ubah nama agar lebih umum jika menangani landing page
    
    @GetMapping("/")
    public String landingPage() {
        return "index"; // Mengarahkan ke landing_page.html di folder templates
    }

    @GetMapping("/services")
    public String servicesPage() {
        return "services"; // Contoh: services.html
    }
}
