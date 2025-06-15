package pbo.autocare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pbo.autocare.model.ServiceOrder;
import pbo.autocare.model.Technician;
import pbo.autocare.model.Transaction;
import pbo.autocare.service.ServiceOrderService; 
import pbo.autocare.service.TransactionService;

import java.util.List;
import java.util.Optional; 

@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired 
    private ServiceOrderService serviceOrderService;

    @GetMapping("/dashboard")
    public String staffDashboard() {
        return "staff_dashboard";
    }

    @GetMapping("/orders")
    public String listServiceOrders(Model model) {
        List<ServiceOrder> orders = serviceOrderService.getAllServiceOrders();
        model.addAttribute("orders", orders);
        model.addAttribute("orderStatuses", ServiceOrder.OrderStatus.values());
        model.addAttribute("allTechnicians", serviceOrderService.getAllTechnicians()); 
        return "staff/serviceOrders"; 
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

        return "redirect:/staff/orders/detail/" + orderId;
    }

    @GetMapping("/orders/detail/{id}")
    public String viewOrderDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<ServiceOrder> orderOptional = serviceOrderService.getServiceOrderById(id);
        if (orderOptional.isPresent()) {
            ServiceOrder order = orderOptional.get();
            model.addAttribute("order", order);
            model.addAttribute("orderStatuses", ServiceOrder.OrderStatus.values());

            List<Technician> availableTechnicians = serviceOrderService.getTechniciansByServiceSpecialization(order.getService().getId());
            model.addAttribute("availableTechnicians", availableTechnicians);

            return "staff/serviceOrderDetail";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Order tidak ditemukan.");
            return "redirect:/staff/orders"; 
        }
    }

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/transaction")
    public String showTransactions(Model model) {
        List<Transaction> transactions = transactionService.getAllTransactions();
        model.addAttribute("transactions", transactions);
        return "staff/transaction";
    }

    @GetMapping("/transaction/edit/{id}")
    public String showEditTransactionForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Transaction> transaction = transactionService.getTransactionById(id);
        if (transaction.isPresent()) {
            model.addAttribute("transaction", transaction.get());
            model.addAttribute("statusOptions", Transaction.TransactionStatus.values());
            model.addAttribute("paymentMethodOptions", Transaction.PaymentMethod.values());
            return "staff/edit-transaction";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Transaction not found!");
            return "redirect:/staff/transaction";
        }
    }

    @PostMapping("/transaction/update/{id}")
    public String updateTransaction(@PathVariable("id") Long id,
                                    @RequestParam("transactionStatus") String transactionStatusString,
                                    @RequestParam("paymentMethod") String paymentMethodString, 
                                    RedirectAttributes redirectAttributes) {
        try {
            Transaction.TransactionStatus newStatus = Transaction.TransactionStatus.valueOf(transactionStatusString);
            Transaction.PaymentMethod newPaymentMethod = Transaction.PaymentMethod.valueOf(paymentMethodString); 

            Transaction updatedTransaction = transactionService.updateTransactionStatusAndPaymentMethod(
                    id, newStatus, newPaymentMethod); 

            if (updatedTransaction != null) {
                redirectAttributes.addFlashAttribute("successMessage", "Transaction updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Transaction not found for update!");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid transaction status or payment method provided! Error: " + e.getMessage());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating transaction: " + e.getMessage());

        }
        return "redirect:/staff/transaction";
    }

    @GetMapping("/transactions/print/{serviceOrderId}") 
    public String printReservationReceipt(@PathVariable Long serviceOrderId, Model model, RedirectAttributes redirectAttributes) {
        Optional<ServiceOrder> serviceOrderOptional = serviceOrderService.getServiceOrderById(serviceOrderId);

        if (serviceOrderOptional.isPresent()) {
            ServiceOrder serviceOrder = serviceOrderOptional.get();
            Optional<Transaction> transactionOptional = transactionService.getTransactionByServiceOrder(serviceOrder); 

            if (transactionOptional.isPresent()) {
                Transaction transaction = transactionOptional.get();
                model.addAttribute("transaction", transaction);
                model.addAttribute("currentDate", new java.util.Date()); 
                return "customer/print_receipt"; 
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Transaksi untuk order ini tidak ditemukan.");
                return "redirect:/staff/transaction"; 
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Order tidak ditemukan.");
            return "redirect:/staff/transaction"; 
        }
    }
}