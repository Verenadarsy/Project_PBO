package pbo.autocare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; // Tambahkan ini
import org.springframework.web.bind.annotation.PostMapping; // Tambahkan ini
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Tambahkan ini

import pbo.autocare.dto.ServiceOrderFormDTO;
import pbo.autocare.model.ServiceOrder;
import pbo.autocare.service.ServiceOrderService; // Pastikan ini mengacu ke interface

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private ServiceOrderService serviceOrderService; // Ini harus ServiceOrderServiceImpl yang di-inject

    @GetMapping("/dashboard")
    public String customerDashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "customer_dashboard";
    }

    @GetMapping("/new-reservation") // URL untuk menampilkan form
    public String newTransactionForm(Model model) { // Harus ada parameter Model
        model.addAttribute("serviceOrderFormDTO", new ServiceOrderFormDTO()); // HARUS DTO!
        model.addAttribute("users", serviceOrderService.getAllUsers()); // Perlu data users
        model.addAttribute("vehicles", serviceOrderService.getAllVehicles()); // Perlu data vehicles
        model.addAttribute("services", serviceOrderService.getAllServices()); // Perlu data services
        return "service_order_form"; // Mengarahkan ke HTML ini
    }

    // Ini adalah controller POST untuk menerima data dari form di atas
    @PostMapping("/save-reservation") // Sesuaikan dengan th:action di HTML Anda
    public String saveNewReservation(@ModelAttribute ServiceOrder serviceOrder, RedirectAttributes redirectAttributes) {
        try {
            // Ketika serviceOrder datang dari form, properti User, Vehicle, ServiceItem
            // di dalamnya hanya akan memiliki ID yang terisi (karena th:field="*{user.id}").
            // Spring Data JPA biasanya bisa menangani ini jika ID yang dikirim valid
            // dan relasi ManyToOne sudah benar di model.
            // Namun, jika Anda mengalami error, Anda mungkin perlu mengambil objek penuhnya secara manual:

            // Contoh mengambil objek penuh secara manual (jika diperlukan):
            // User selectedUser = serviceOrderService.getUserById(serviceOrder.getUser().getId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
            // serviceOrder.setUser(selectedUser);
            // ... lakukan hal serupa untuk vehicle dan serviceItem

            // Logic untuk mengisi nilai default yang tidak ada di form atau di set di backend
            if (serviceOrder.getId() == null) { // Jika ini adalah entitas baru
                serviceOrder.setOrderStatus(ServiceOrder.OrderStatus.PENDING);
                // createdAt dan updatedAt akan diatur di ServiceOrderServiceImpl
            }

            serviceOrderService.saveServiceOrder(serviceOrder);
            redirectAttributes.addFlashAttribute("successMessage", "Reservasi berhasil dibuat!");
            return "redirect:/customer/dashboard"; // Redirect ke dashboard setelah sukses
        } catch (Exception e) {
            // Log error untuk debugging
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal membuat reservasi: " + e.getMessage());
            return "redirect:/customer/new-reservation"; // Kembali ke form jika ada error
        }
    }

    @Controller
    public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
        @RequestMapping("/error")
        public String handleError() {
            return "error"; // return view bernama error.html
        }
    }


    // ... metode lain untuk /my-vehicles, /my-transactions, dll.
}