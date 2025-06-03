package pbo.autocare.controller;

import pbo.autocare.model.ServiceOrder;
import pbo.autocare.model.User;
import pbo.autocare.model.Vehicle;
import pbo.autocare.model.ServiceItem;
import pbo.autocare.dto.ServiceOrderFormDTO; // Import DTO
import pbo.autocare.repository.ServiceOrderRepository;
import pbo.autocare.repository.UserRepository;
import pbo.autocare.repository.VehicleRepository;
import pbo.autocare.repository.ServiceItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Untuk pesan flash

import jakarta.validation.Valid; // Untuk validasi DTO
import org.springframework.validation.BindingResult; // Untuk menangani hasil validasi

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/serviceorders")
public class ServiceOrderController {

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ServiceItemRepository serviceItemRepository;

    @GetMapping("/new-reservation")
    public String showNewServiceOrderForm(Model model) {
        // Kita tidak lagi menambahkan ServiceOrder kosong ke model secara langsung untuk binding
        // Kita akan menambahkan DTO
        model.addAttribute("serviceOrderFormDTO", new ServiceOrderFormDTO());

        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);

        List<Vehicle> vehicles = vehicleRepository.findAll();
        model.addAttribute("vehicles", vehicles);

        List<ServiceItem> services = serviceItemRepository.findAll();
        model.addAttribute("services", services);

        return "serviceorder/add-service-order";
    }

    @PostMapping("/save")
    public String saveServiceOrder(@ModelAttribute("serviceOrderFormDTO") @Valid ServiceOrderFormDTO serviceOrderFormDTO,
                                   BindingResult bindingResult, // Penting untuk validasi
                                   RedirectAttributes redirectAttributes) {

        // Menangani error validasi dari DTO
        if (bindingResult.hasErrors()) {
            // Jika ada error, kembali ke form dengan pesan error
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.serviceOrderFormDTO", bindingResult);
            redirectAttributes.addFlashAttribute("serviceOrderFormDTO", serviceOrderFormDTO);
            return "redirect:/serviceorders/new-reservation";
        }

        try {
            // Memuat objek entitas berdasarkan ID dari DTO
            Optional<User> userOptional = userRepository.findById(serviceOrderFormDTO.getUserId());
            Optional<Vehicle> vehicleOptional = vehicleRepository.findById(serviceOrderFormDTO.getVehicleTypeId());
            Optional<ServiceItem> serviceOptional = serviceItemRepository.findById(serviceOrderFormDTO.getServiceId());

            if (userOptional.isPresent() && vehicleOptional.isPresent() && serviceOptional.isPresent()) {
                // Buat instance ServiceOrder dan isi dari DTO
                ServiceOrder serviceOrder = new ServiceOrder(); // Gunakan konstruktor default kosong
                serviceOrder.setUser(userOptional.get());
                serviceOrder.setCustomerName(serviceOrderFormDTO.getCustomerName());
                serviceOrder.setCustomerContact(serviceOrderFormDTO.getCustomerContact());
                serviceOrder.setCustomerAddress(serviceOrderFormDTO.getCustomerAddress());
                serviceOrder.setVehicleModelName(serviceOrderFormDTO.getVehicleModelName());
                serviceOrder.setVehicleType(vehicleOptional.get());
                serviceOrder.setLicensePlate(serviceOrderFormDTO.getLicensePlate());
                serviceOrder.setService(serviceOptional.get());
                serviceOrder.setFinalPrice(serviceOrderFormDTO.getFinalPrice());
                serviceOrder.setOrderNotes(serviceOrderFormDTO.getOrderNotes());

                // Atur createdAt dan updatedAt secara manual jika tidak menggunakan Auditing
                // serviceOrder.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                // serviceOrder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                // Atau biarkan constructor default ServiceOrder yang mengaturnya jika sudah Anda tambahkan.

                serviceOrderRepository.save(serviceOrder);
                redirectAttributes.addFlashAttribute("successMessage", "Reservasi berhasil disimpan!");
                return "redirect:/serviceorders/list";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Error: Data terkait (User, Kendaraan, atau Layanan) tidak ditemukan.");
                return "redirect:/serviceorders/new-reservation";
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan server: " + e.getMessage());
            return "redirect:/serviceorders/new-reservation";
        }
    }

    // ... (metode listServiceOrders jika ada)

    // Tambahkan method untuk menampilkan daftar service order (opsional, tapi bagus untuk navigasi)
    @GetMapping("/list")
    public String listServiceOrders(Model model) {
        List<ServiceOrder> serviceOrders = serviceOrderRepository.findAll();
        model.addAttribute("serviceOrders", serviceOrders);
        return "serviceorder/list-service-orders"; // Contoh nama file HTML
    }
}