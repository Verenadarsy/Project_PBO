package pbo.autocare.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
// import com.yourcompany.autocarepro.service.ServiceOrderService; // Untuk transaksi customer

@Controller
@RequestMapping("/customer")
public class CustomerController {

    // private final ServiceOrderService serviceOrderService; // Inject jika sudah ada

    // public CustomerController(ServiceOrderService serviceOrderService) {
    //     this.serviceOrderService = serviceOrderService;
    // }

    @GetMapping("/dashboard")
    public String customerDashboard(Authentication authentication, Model model) {
        // authentication.getName() akan mendapatkan username dari user yang login
        model.addAttribute("username", authentication.getName());
        return "customer_dashboard";
    }

    // User hanya bisa membuat dan melihat transaksi miliknya sendiri
    @GetMapping("/my-transactions")
    public String myTransactions(Authentication authentication, Model model) {
        // String username = authentication.getName();
        // Dapatkan transaksi berdasarkan username customer
        // List<ServiceOrder> myOrders = serviceOrderService.getServiceOrdersByCustomerUsername(username);
        // model.addAttribute("myOrders", myOrders);
        return "customer_my_transactions"; // Contoh: customer_my_transactions.html
    }

    // Form untuk membuat transaksi baru (service order)
    @GetMapping("/new-transaction")
    public String newTransactionForm(Model model) {
        // model.addAttribute("serviceOrder", new ServiceOrder()); // Objek form
        return "service_order_form"; // Menggunakan form yang sama dengan admin, tapi dengan batasan
    }

    // @PostMapping("/new-transaction")
    // public String saveNewTransaction(@ModelAttribute("serviceOrder") ServiceOrder serviceOrder, Authentication authentication) {
    //     // Set customer otomatis berdasarkan user yang login
    //     // serviceOrder.setCustomer(customerRepository.findByUsername(authentication.getName()).orElse(null));
    //     // serviceOrderService.saveServiceOrder(serviceOrder);
    //     return "redirect:/customer/my-transactions";
    // }
}