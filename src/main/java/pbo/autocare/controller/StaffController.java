package pbo.autocare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @GetMapping("/dashboard")
    public String staffDashboard() {
        return "staff_dashboard"; // Mengarahkan ke staff_dashboard.html
    }
}

