// src/main/java/pbo/autocare/controller/TechnicianController.java
package pbo.autocare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pbo.autocare.model.ServiceOrder;
import pbo.autocare.model.Technician;
import pbo.autocare.model.User;
import pbo.autocare.service.ServiceOrderService;
import pbo.autocare.service.UserService;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/technician")
public class TechnicianController { // Nama kelas TechnicianController (sesuai yang Anda berikan)

    @Autowired
    private ServiceOrderService serviceOrderService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String showTechnicianDashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        User loggedInUser = userService.findUserByUsername(username)
                                .orElseThrow(() -> new RuntimeException("Logged in user not found!"));

        if (!(loggedInUser instanceof Technician)) {
            return "redirect:/logout";
        }

        Technician currentTechnician = (Technician) loggedInUser;
        List<ServiceOrder> assignedOrders = serviceOrderService.getServiceOrdersByAssignedTechnician(currentTechnician);

        // Statistik
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);

        long todayCount = assignedOrders.stream()
            .filter(order -> order.getCreatedAt().toLocalDateTime().toLocalDate().equals(today))
            .count();

        long thisMonthCount = assignedOrders.stream()
            .filter(order -> {
                LocalDate date = order.getCreatedAt().toLocalDateTime().toLocalDate();
                return !date.isBefore(firstDayOfMonth) && !date.isAfter(today);
            })
            .count();

        long inProgressCount = assignedOrders.stream()
            .filter(order -> order.getOrderStatus() == ServiceOrder.OrderStatus.IN_PROGRESS)
            .count();

        long doneTodayCount = assignedOrders.stream()
            .filter(order -> order.getOrderStatus() == ServiceOrder.OrderStatus.COMPLETED &&
                            order.getUpdatedAt() != null &&
                            order.getUpdatedAt().toLocalDateTime().toLocalDate().equals(today))
            .count();

        // Atribut ke view
        String specializationDesc = (currentTechnician.getSpecialization() != null)
                ? currentTechnician.getSpecialization().getDescription()
                : "Unspecified";

        model.addAttribute("technician", currentTechnician);
        model.addAttribute("specializationDesc", specializationDesc);
        model.addAttribute("assignedOrders", assignedOrders);
        model.addAttribute("pageTitle", "Technician Dashboard");
        model.addAttribute("orderStatuses", ServiceOrder.OrderStatus.values());

        model.addAttribute("todayCount", todayCount);
        model.addAttribute("thisMonthCount", thisMonthCount);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("doneTodayCount", doneTodayCount);

        return "technician_dashboard";
    }


    @PostMapping("/dashboard/order/update-status/{id}")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam("orderStatus") String newStatusString,
                                    RedirectAttributes redirectAttributes,
                                    Authentication authentication) {
        String username = authentication.getName();
        User loggedInUser = userService.findUserByUsername(username)
                                .orElseThrow(() -> new RuntimeException("Logged in user not found!"));

        if (!(loggedInUser instanceof Technician)) {
            return "redirect:/logout";
        }

        Technician currentTechnician = (Technician) loggedInUser;
        Optional<ServiceOrder> orderOptional = serviceOrderService.getServiceOrderById(id);

        if (orderOptional.isPresent()) {
            ServiceOrder order = orderOptional.get();

            if (order.getAssignedTechnician() != null &&
                order.getAssignedTechnician().getId().equals(currentTechnician.getId())) {
                try {
                    ServiceOrder.OrderStatus newStatus = ServiceOrder.OrderStatus.valueOf(newStatusString);
                    order.setOrderStatus(newStatus);
                    order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    serviceOrderService.saveServiceOrder(order);

                    redirectAttributes.addFlashAttribute("successMessage", "Status order #" + id + " berhasil diperbarui!");
                } catch (IllegalArgumentException e) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Status order tidak valid.");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Anda tidak memiliki izin untuk mengubah order ini.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Order tidak ditemukan.");
        }

        return "redirect:/technician/dashboard"; // Pastikan path ini sesuai dengan routing-mu
    }

}