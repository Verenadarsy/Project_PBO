package pbo.autocare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import pbo.autocare.model.Customer;
import pbo.autocare.service.UserService;

@Controller
public class AuthController {

    private final UserService userService; 

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("customer", new Customer()); 
        return "register";
    }

    @PostMapping("/register")
    public String registerCustomer(
            @Valid @ModelAttribute("customer") Customer customer, 
            BindingResult bindingResult, 
            Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {

            if (userService.findUserByUsername(customer.getUsername()).isPresent()) {
                model.addAttribute("error", "Username ini sudah terdaftar. Mohon gunakan username lain.");
                return "register";
            }

            userService.registerNewCustomer(customer);
            return "redirect:/login?registered"; 
        } catch (Exception e) {

            model.addAttribute("error", "Registrasi gagal karena kesalahan sistem. Silakan coba lagi.");
           
            return "register"; 
        }
    }
}