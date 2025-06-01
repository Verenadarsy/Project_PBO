// src/main/java/pbo/autocare/controller/TechnicianController.java
package pbo.autocare.controller;
import pbo.autocare.model.User;
import pbo.autocare.service.UserService; // <-- Import interface UserService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/technician")
public class TechnicianController {

    @Autowired // Menyuntikkan interface UserService
    private UserService userService; // <-- Ganti tipe menjadi UserService

    @GetMapping("/dashboard")
    public String technicianDashboard() {
    return "technician_dashboard"; // Mengarahkan ke technician_dashboard.html
    }
    
    @GetMapping("/list")
    public String showTechnicianList(Model model) {
        // Panggil service layer untuk mendapatkan daftar Teknisi
        List<User> technicianList = userService.getAllTechnicians(); // <-- Panggil metode dari UserService

        // Tambahkan daftar teknisi ke objek Model
        model.addAttribute("technicians", technicianList);

        return "technician_list";
    }
}