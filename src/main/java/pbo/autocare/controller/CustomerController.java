package pbo.autocare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
// import pbo.autocare.service.ServiceOrderService;

import pbo.autocare.model.ServiceOrder;
import pbo.autocare.service.ServiceOrderService;

@Controller
@RequestMapping("/customer")
public class CustomerController {

 //ivate final ServiceOrderService serviceOrderService;

    // public CustomerController(ServiceOrderService serviceOrderService) {
    //     this.serviceOrderService = serviceOrderService;
    // }

    @GetMapping("/dashboard")
    public String customerDashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "customer_dashboard";
    }

    // @GetMapping("/my-transactions")
    // public String myTransactions(Authentication authentication, Model model) {
    //     String username = authentication.getName();
    //     model.addAttribute("myOrders", serviceOrderService.getOrdersByUserUsername(username)); // <--- CORRECTED LINE
    //     return "customer_my_transactions";
    // }

    // Di Controller Anda (misalnya ServiceOrderController)
    // Pastikan ServiceOrderService sudah di-Autowired
    @Autowired
    private ServiceOrderService serviceOrderService;

    @GetMapping("/new-reservation") // URL yang baru untuk form
    public String newTransactionForm(Model model) {
        // 1. Tambahkan objek ServiceOrder kosong untuk binding form
        model.addAttribute("serviceOrder", new ServiceOrder()); // <--- HAPUS KOMENTAR INI!

        // 2. Ambil dan tambahkan data untuk dropdown
        //    Pastikan ServiceOrderService Anda memiliki method ini
        model.addAttribute("users", serviceOrderService.getAllUsers());
        model.addAttribute("vehicles", serviceOrderService.getAllVehicles());
        model.addAttribute("services", serviceOrderService.getAllServices());

        return "service_order_form"; // Mengarahkan ke service_order_form.html
    }
}