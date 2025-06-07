package pbo.autocare.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import pbo.autocare.model.ServiceItem;
import pbo.autocare.model.ServiceOrder;
import pbo.autocare.model.User;
import pbo.autocare.model.Vehicle;
import pbo.autocare.repository.ServiceOrderRepository;
import pbo.autocare.repository.ServiceRepository;
import pbo.autocare.repository.UserRepository;
import pbo.autocare.repository.VehicleRepository;

@Service
public class ServiceOrderServiceImpl implements ServiceOrderService { 

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

   @Autowired
    private ServiceRepository serviceRepository;

    @Override
    public List<ServiceItem> getAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    public Optional<ServiceOrder> findById(Long id) {
        return serviceOrderRepository.findById(id);
    }

    @Override
    public List<ServiceOrder> findAll() {
        return serviceOrderRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        serviceOrderRepository.deleteById(id);
    }

    @Transactional // <-- Pastikan ini ada di sini
    @Override
    public ServiceOrder saveServiceOrder(ServiceOrder serviceOrder) {
        if (serviceOrder.getId() == null) {
            serviceOrder.setOrderStatus(ServiceOrder.OrderStatus.PENDING);
            serviceOrder.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }
        serviceOrder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return serviceOrderRepository.save(serviceOrder);
    }

    @Override
    public void saveOrder(ServiceOrder serviceOrder) {
        saveServiceOrder(serviceOrder);
    }

    @Override
    public long countOrdersThisMonth() {
        // Mendapatkan tanggal dan waktu saat ini di zona waktu sistem
        LocalDateTime now = LocalDateTime.now();
        
        // Mendapatkan awal bulan saat ini (tanggal 1, jam 00:00:00)
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth())
                                        .withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        // Konversi LocalDateTime ke Timestamp untuk digunakan dalam query JPA
        Timestamp startOfMonthTimestamp = Timestamp.from(startOfMonth.atZone(ZoneId.systemDefault()).toInstant());

        // Panggil metode dari repository untuk menghitung order setelah tanggal ini
        return serviceOrderRepository.countByCreatedAtAfter(startOfMonthTimestamp);
    }
}