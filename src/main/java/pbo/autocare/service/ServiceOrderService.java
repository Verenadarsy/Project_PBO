// pbo.autocare.service.ServiceOrderService.java (interface)
package pbo.autocare.service;

import pbo.autocare.model.ServiceOrder;
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
    Optional<ServiceOrder> findById(Long id); // Jika ditambahkan
    List<ServiceOrder> findAll(); // Jika ditambahkan
    void deleteById(Long id); // Jika ditambahkan
    void saveOrder(ServiceOrder serviceOrder);
    long countOrdersThisMonth();
    List<ServiceOrder> getAllServiceOrders();
    void createServiceOrder(ServiceOrder serviceOrder);
    Optional<ServiceOrder> getServiceOrderById(Long id);
    void deleteServiceOrder(Long id);
}