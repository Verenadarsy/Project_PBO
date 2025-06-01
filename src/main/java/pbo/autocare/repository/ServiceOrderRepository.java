package pbo.autocare.repository;

import pbo.autocare.model.ServiceItem;
import pbo.autocare.model.ServiceOrder;
import pbo.autocare.model.ServiceOrder.OrderStatus;
import pbo.autocare.model.User; // Make sure 'User' is correctly imported and used in your ServiceOrder entity
import pbo.autocare.model.Vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
    // Other methods...

    // Use findByUser_Username to query by the username of the associated User object
    List<ServiceOrder> findByUser_Username(String username); // <--- CORRECTED LINE

    // If you explicitly have a 'customer' field in ServiceOrder and 'username' in Customer:
    // List<ServiceOrder> findByCustomer_Username(String username);

    // Remove the old problematic line:
    // List<ServiceOrder> findByCustomerUsername(String username); // <-- DELETE THIS LINE

    // Other methods you have:
    List<ServiceOrder> findByUser(User user);
    List<ServiceOrder> findByUserId(Long userId);
    List<ServiceOrder> findByOrderStatus(OrderStatus orderStatus);
    List<ServiceOrder> findByLicensePlateContainingIgnoreCase(String licensePlate);
    List<ServiceOrder> findByUserAndOrderStatus(User user, OrderStatus orderStatus);
    List<ServiceOrder> findByUserIdAndOrderStatus(Long userId, OrderStatus orderStatus);
    List<ServiceOrder> findByService(ServiceItem service);
    List<ServiceOrder> findByVehicleType(Vehicle vehicleType);
}