package pbo.autocare.service;

import pbo.autocare.model.ServiceOrder;
import pbo.autocare.model.ServiceOrder.OrderStatus;
import pbo.autocare.model.Technician;
import pbo.autocare.model.User;
import pbo.autocare.model.Vehicle;
import pbo.autocare.model.ServiceItem;

import java.util.List;
import java.util.Optional;

public interface ServiceOrderService {
    List<User> getAllUsers();
    List<Vehicle> getAllVehicles();
    List<ServiceItem> getAllServices();
    ServiceOrder saveServiceOrder(ServiceOrder serviceOrder);
    Optional<ServiceOrder> findById(Long id); 
    List<ServiceOrder> findAll(); 
    void deleteById(Long id); 
    void saveOrder(ServiceOrder serviceOrder);
    long countOrdersThisMonth();
    List<ServiceOrder> getAllServiceOrders();
    void createServiceOrder(ServiceOrder serviceOrder);
    Optional<ServiceOrder> getServiceOrderById(Long id);
    void deleteServiceOrder(Long id);
    ServiceOrder updateServiceOrder(Long id, ServiceOrder updatedServiceOrder);
    Object getAllTechnicians();
    Optional<ServiceOrder> assignTechnicianToOrder(Long orderId, Long technicianId);
    List<Technician> getTechniciansByServiceSpecialization(Long id);
    Optional<ServiceOrder> updateOrderStatus(Long id, OrderStatus newStatus);
    List<ServiceOrder> getServiceOrdersByAssignedTechnician(Technician assignedTechnician);
}