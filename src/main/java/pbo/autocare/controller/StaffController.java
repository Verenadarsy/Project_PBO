// src/main/java/pbo/autocare/controller/StaffController.java
package pbo.autocare.controller;

//import pbo.autocare.model.Staff;
//import pbo.autocare.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model; // <--- TAMBAHKAN BARIS INI
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//import java.util.List;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @GetMapping("/dashboard")
    public String staffDashboard() {
    return "staff_dashboard"; // Mengarahkan ke staff_dashboard.html
    }
}