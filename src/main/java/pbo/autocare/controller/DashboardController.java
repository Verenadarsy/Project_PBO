package pbo.autocare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController { 
    
    @GetMapping("/")
    public String landingPage() {
        return "index";
    }
}
