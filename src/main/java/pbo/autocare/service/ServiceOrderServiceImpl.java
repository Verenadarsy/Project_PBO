package pbo.autocare.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import pbo.autocare.model.ServiceItem;
import pbo.autocare.model.ServiceOrder;
import pbo.autocare.model.Technician;
import pbo.autocare.model.Transaction; 
import pbo.autocare.model.User;
import pbo.autocare.model.Vehicle;
import pbo.autocare.repository.ServiceItemRepository;
import pbo.autocare.repository.ServiceOrderRepository;
import pbo.autocare.repository.ServiceRepository;
import pbo.autocare.repository.TechnicianRepository;
import pbo.autocare.repository.TransactionRepository; 
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

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private TransactionRepository transactionRepository; 

    @Autowired
    private TechnicianRepository technicianRepository;

    @Autowired
    private ServiceItemRepository serviceItemRepository; 

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

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
    @Transactional
    public void deleteById(Long id) {

        serviceOrderRepository.deleteById(id);
    }

    /**
     * @param serviceOrder objek ServiceOrder yang akan disimpan.
     * @return ServiceOrder yang telah disimpan.
     */
    @Transactional
    @Override
    public ServiceOrder saveServiceOrder(ServiceOrder serviceOrder) {
        boolean isNewOrder = serviceOrder.getId() == null;

        if (isNewOrder) {
            serviceOrder.setOrderStatus(ServiceOrder.OrderStatus.PENDING);
            serviceOrder.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }
        serviceOrder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        ServiceOrder savedServiceOrder = serviceOrderRepository.save(serviceOrder);

        if (isNewOrder) {
            Transaction newTransaction = new Transaction();
            newTransaction.setServiceOrder(savedServiceOrder); 
            newTransaction.setAmount(savedServiceOrder.getFinalPrice()); 
            newTransaction.setTransactionDate(new Timestamp(System.currentTimeMillis()));
            newTransaction.setPaymentMethod(Transaction.PaymentMethod.UNSPECIFIED);
            newTransaction.setTransactionStatus(Transaction.TransactionStatus.PENDING); 
            newTransaction.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            newTransaction.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            transactionRepository.save(newTransaction);
        } else {
            // Logika update Transaction terkait jika finalPrice berubah saat ServiceOrder diupdate
        }

        return savedServiceOrder;
    }

    @Override
    public void saveOrder(ServiceOrder serviceOrder) {
        saveServiceOrder(serviceOrder);
    }

    @Override
    public long countOrdersThisMonth() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth())
                                         .withHour(0).withMinute(0).withSecond(0).withNano(0);
        Timestamp startOfMonthTimestamp = Timestamp.from(startOfMonth.atZone(ZoneId.systemDefault()).toInstant());

        return serviceOrderRepository.countByCreatedAtAfter(startOfMonthTimestamp);
    }

    @Override
    public List<ServiceOrder> getAllServiceOrders() {
        return serviceOrderRepository.findAll();
    }

    @Override
    public Optional<ServiceOrder> getServiceOrderById(Long id) {
        return serviceOrderRepository.findById(id);
    }

    @Transactional
    @Override
    public ServiceOrder updateServiceOrder(Long id, ServiceOrder updatedServiceOrder) {
        return serviceOrderRepository.findById(id)
                .map(existingOrder -> {
                    existingOrder.setUser(updatedServiceOrder.getUser());
                    existingOrder.setCustomerName(updatedServiceOrder.getCustomerName());
                    existingOrder.setCustomerContact(updatedServiceOrder.getCustomerContact());
                    existingOrder.setCustomerAddress(updatedServiceOrder.getCustomerAddress());
                    existingOrder.setVehicleModelName(updatedServiceOrder.getVehicleModelName());
                    existingOrder.setVehicleType(updatedServiceOrder.getVehicleType());
                    existingOrder.setLicensePlate(updatedServiceOrder.getLicensePlate());
                    existingOrder.setService(updatedServiceOrder.getService());
                    existingOrder.setFinalPrice(updatedServiceOrder.getFinalPrice());
                    existingOrder.setSelectedDurationDays(updatedServiceOrder.getSelectedDurationDays());
                    existingOrder.setServiceName(updatedServiceOrder.getServiceName());
                    existingOrder.setOrderStatus(updatedServiceOrder.getOrderStatus());
                    existingOrder.setOrderNotes(updatedServiceOrder.getOrderNotes());

                    return saveServiceOrder(existingOrder);
                }).orElseThrow(() -> new RuntimeException("Service Order not found with id " + id));
    }

    public void deleteServiceOrder(Long id) {
        serviceOrderRepository.deleteById(id);
    }

    public void createServiceOrder(ServiceOrder serviceOrder) {
    serviceOrderRepository.save(serviceOrder); 
    }

    public Optional<ServiceOrder> assignTechnicianToOrder(Long orderId, Long technicianId) {
        Optional<ServiceOrder> orderOptional = serviceOrderRepository.findById(orderId);
        Optional<Technician> technicianOptional = technicianRepository.findById(technicianId);

        if (orderOptional.isPresent() && technicianOptional.isPresent()) {
            ServiceOrder order = orderOptional.get();
            Technician technician = technicianOptional.get();
            order.setAssignedTechnician(technician);
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            return Optional.of(serviceOrderRepository.save(order));
        }
        return Optional.empty();
    }

    public List<Technician> getAllTechnicians() {
        return technicianRepository.findAll();
    }

    /**
     * @param serviceItemId 
     * @return 
     */
    public List<Technician> getTechniciansByServiceSpecialization(Long serviceItemId) {
        Optional<ServiceItem> serviceItemOptional = serviceItemRepository.findById(serviceItemId);

        if (serviceItemOptional.isPresent()) {
            ServiceItem serviceItem = serviceItemOptional.get();
       
            if (serviceItem.getRequiredSpecialization() != null && serviceItem.getRequiredSpecialization().getCode() != null) {
                String requiredCode = serviceItem.getRequiredSpecialization().getCode();
             
                return technicianRepository.findBySpecialization_Code(requiredCode);
            }

            return technicianRepository.findAll(); 
        }
        return Collections.emptyList(); 
    }

    @Override 
    public Optional<ServiceOrder> updateOrderStatus(Long id, ServiceOrder.OrderStatus newStatus) {
    
        return serviceOrderRepository.findById(id)
                .map(order -> {
                    order.setOrderStatus(newStatus);
                    order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    return serviceOrderRepository.save(order);
                });
    }
    
    @Override 
    public List<ServiceOrder> getServiceOrdersByAssignedTechnician(Technician assignedTechnician) {
        return serviceOrderRepository.findByAssignedTechnician(assignedTechnician);
    }
}