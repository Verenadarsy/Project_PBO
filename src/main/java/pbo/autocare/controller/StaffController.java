// src/main/java/pbo/autocare/controller/StaffController.java
package pbo.autocare.controller;

import org.springframework.beans.factory.annotation.Autowired; // <-- PENTING: Import ini
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // <-- PENTING: Import ini
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // <-- PENTING: Untuk update status dan assign teknisi
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // <-- PENTING: Untuk menerima parameter form
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pbo.autocare.model.ServiceOrder;
import pbo.autocare.model.Technician;
import pbo.autocare.service.ServiceOrderService; // <-- PENTING: Import ini

import java.util.List; // <-- PENTING: Import ini
import java.util.Optional; // <-- PENTING: Import ini

@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired // <-- PENTING: Suntikkan ServiceOrderService di sini
    private ServiceOrderService serviceOrderService;

    @GetMapping("/dashboard")
    public String staffDashboard() {
        return "staff_dashboard"; // Mengarahkan ke staff_dashboard.html
    }

    // --- BARU DITAMBAHKAN/DIKOREKSI DARI SEBELUMNYA ---

    @GetMapping("/orders")
    public String listServiceOrders(Model model) {
        List<ServiceOrder> orders = serviceOrderService.getAllServiceOrders();
        model.addAttribute("orders", orders);
        model.addAttribute("orderStatuses", ServiceOrder.OrderStatus.values());
        model.addAttribute("allTechnicians", serviceOrderService.getAllTechnicians()); // Mungkin tidak dipakai langsung di tabel, tapi bagus untuk konteks
        return "staff/serviceOrders"; // Mengarahkan ke template HTML daftar order
    }

    @PostMapping("/orders/update-status/{id}")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam("status") ServiceOrder.OrderStatus newStatus,
                                    RedirectAttributes redirectAttributes) {
        Optional<ServiceOrder> updatedOrder = serviceOrderService.updateOrderStatus(id, newStatus);
        if (updatedOrder.isPresent()) {
            redirectAttributes.addFlashAttribute("successMessage", "Status order berhasil diupdate.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Order tidak ditemukan.");
        }
        // Redirect ke halaman detail order, bukan ke daftar semua order (lebih UX friendly)
        return "redirect:/staff/orders/detail/" + id;
    }

    @PostMapping("/orders/assign-technician/{orderId}")
    public String assignTechnician(@PathVariable Long orderId,
                                   @RequestParam("technicianId") Long technicianId,
                                   RedirectAttributes redirectAttributes) {
        Optional<ServiceOrder> updatedOrder = serviceOrderService.assignTechnicianToOrder(orderId, technicianId);
        if (updatedOrder.isPresent()) {
            redirectAttributes.addFlashAttribute("successMessage", "Teknisi berhasil ditugaskan ke order.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal menugaskan teknisi. Order atau teknisi tidak ditemukan.");
        }
        // Redirect ke halaman detail order
        return "redirect:/staff/orders/detail/" + orderId;
    }

    @GetMapping("/orders/detail/{id}")
    public String viewOrderDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<ServiceOrder> orderOptional = serviceOrderService.getServiceOrderById(id);
        if (orderOptional.isPresent()) {
            ServiceOrder order = orderOptional.get();
            model.addAttribute("order", order);
            model.addAttribute("orderStatuses", ServiceOrder.OrderStatus.values());

            // Panggil method yang sudah disesuaikan untuk mendapatkan teknisi yang relevan
            List<Technician> availableTechnicians = serviceOrderService.getTechniciansByServiceSpecialization(order.getService().getId());
            model.addAttribute("availableTechnicians", availableTechnicians);

            return "staff/serviceOrderDetail";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Order tidak ditemukan.");
            return "redirect:/staff/orders"; // Kembali ke daftar order jika tidak ditemukan
        }
    }
}