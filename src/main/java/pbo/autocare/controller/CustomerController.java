// src/main/java/pbo/autocare/controller/CustomerController.java

package pbo.autocare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails; // Tambahkan import ini
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pbo.autocare.dto.ServiceOrderFormDTO;
import pbo.autocare.model.ServiceOrder;
import pbo.autocare.model.ServiceItem;
import pbo.autocare.model.Vehicle;
import pbo.autocare.model.User;
import pbo.autocare.repository.ServiceItemRepository;
import pbo.autocare.repository.VehicleRepository;
import pbo.autocare.repository.UserRepository;
import pbo.autocare.service.ServiceOrderService;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.util.Optional;
import java.math.BigDecimal;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private ServiceOrderService serviceOrderService;

    @Autowired
    private ServiceItemRepository serviceItemRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String customerDashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "customer_dashboard";
    }

    @GetMapping("/new-reservation")
    public String newTransactionForm(Authentication authentication, Model model) {
        ServiceOrderFormDTO dto = new ServiceOrderFormDTO();

        // Tidak perlu set userId ke DTO lagi jika kita akan mengambilnya langsung dari Authentication
        // Namun, jika form HTML Anda memiliki th:field="*{userId}" (input hidden), biarkan saja.
        // Spring akan mengisi DTO.userId dengan null jika tidak ada input hidden.
        // Yang penting, kita tidak AKAN MENGANDALKAN DTO.userId di metode POST.

        model.addAttribute("serviceOrderFormDTO", dto);
        model.addAttribute("vehicles", serviceOrderService.getAllVehicles());
        model.addAttribute("services", serviceOrderService.getAllServices());
        return "service_order_form";
    }

     @PostMapping("/save-reservation")
    public String saveNewReservation(@ModelAttribute("serviceOrderFormDTO") @Valid ServiceOrderFormDTO serviceOrderFormDTO,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes,
                                     Authentication authentication) {

        // ... (error handling bindingResult.hasErrors()) ...

        try {
            // ************ PENTING: MENGAMBIL USER DARI SESI LOGIN ************
            User currentUser = null;
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                String username = ((UserDetails) authentication.getPrincipal()).getUsername();
                Optional<User> userOptional = userRepository.findByUsername(username); // Memerlukan findByUsername di UserRepository
                if (userOptional.isPresent()) {
                    currentUser = userOptional.get();
                }
            }

            if (currentUser == null) {
                 // Ini akan terjadi jika pengguna tidak login atau sesi tidak valid
                 redirectAttributes.addFlashAttribute("errorMessage", "Anda harus login untuk membuat reservasi.");
                 return "redirect:/login"; // Redirect ke halaman login atau halaman lain yang sesuai
            }
            // ************ AKHIR PENGAMBILAN USER ************

            Optional<Vehicle> vehicleOptional = vehicleRepository.findById(serviceOrderFormDTO.getVehicleTypeId());
            Optional<ServiceItem> serviceOptional = serviceItemRepository.findById(serviceOrderFormDTO.getServiceId());

            if (vehicleOptional.isPresent() && serviceOptional.isPresent()) {
                ServiceItem selectedService = serviceOptional.get();
                Vehicle selectedVehicle = vehicleOptional.get();

                BigDecimal finalPrice = selectedService.calculateFinalPrice(selectedVehicle);

                ServiceOrder serviceOrder = new ServiceOrder();
                serviceOrder.setUser(currentUser);
                serviceOrder.setCustomerName(serviceOrderFormDTO.getCustomerName());
                serviceOrder.setCustomerContact(serviceOrderFormDTO.getCustomerContact());
                serviceOrder.setCustomerAddress(serviceOrderFormDTO.getCustomerAddress());
                serviceOrder.setVehicleModelName(serviceOrderFormDTO.getVehicleModelName());
                serviceOrder.setVehicleType(selectedVehicle);
                serviceOrder.setLicensePlate(serviceOrderFormDTO.getLicensePlate());

                // ************ PERUBAHAN DI SINI ************
                serviceOrder.setService(selectedService); // Set objek ServiceItem untuk relasi FK
                serviceOrder.setServiceName(selectedService.getServiceName()); // Set nama layanan ke kolom baru
                // ************ AKHIR PERUBAHAN ************

                serviceOrder.setFinalPrice(finalPrice);
                serviceOrder.setOrderNotes(serviceOrderFormDTO.getOrderNotes());

                // serviceOrder.setSelectedDurationDays(serviceOrderFormDTO.getDurationDays()); // Jika ingin menyimpan durasi juga

                serviceOrderService.saveServiceOrder(serviceOrder);

                redirectAttributes.addFlashAttribute("successMessage", "Reservasi berhasil dibuat!");
                return "redirect:/customer/dashboard";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Error: Kendaraan atau Layanan tidak ditemukan. Mohon coba lagi.");
                redirectAttributes.addFlashAttribute("vehicles", serviceOrderService.getAllVehicles());
                redirectAttributes.addFlashAttribute("services", serviceOrderService.getAllServices());
                return "redirect:/customer/new-reservation";
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan server: " + e.getMessage());
            redirectAttributes.addFlashAttribute("vehicles", serviceOrderService.getAllVehicles());
            redirectAttributes.addFlashAttribute("services", serviceOrderService.getAllServices());
            return "redirect:/customer/new-reservation";
        }
    }
}