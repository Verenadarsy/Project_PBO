package pbo.autocare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/technician")
public class TechnicianController {

    @GetMapping("/dashboard")
    public String technicianDashboard() {
        return "technician_dashboard"; // Mengarahkan ke technician_dashboard.html
    }
}
