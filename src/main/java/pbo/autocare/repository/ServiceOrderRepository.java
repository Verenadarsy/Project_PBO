package pbo.autocare.repository;

import pbo.autocare.model.ServiceItem;
import pbo.autocare.model.ServiceOrder;
import pbo.autocare.model.ServiceOrder.OrderStatus;
import pbo.autocare.model.Technician;
import pbo.autocare.model.User; 
import pbo.autocare.model.Vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {

    List<ServiceOrder> findByUser_Username(String username); 
    List<ServiceOrder> findByUser(User user);
    List<ServiceOrder> findByUserId(Long userId);
    List<ServiceOrder> findByOrderStatus(OrderStatus orderStatus);
    List<ServiceOrder> findByLicensePlateContainingIgnoreCase(String licensePlate);
    List<ServiceOrder> findByUserAndOrderStatus(User user, OrderStatus orderStatus);
    List<ServiceOrder> findByUserIdAndOrderStatus(Long userId, OrderStatus orderStatus);
    List<ServiceOrder> findByService(ServiceItem service);
    List<ServiceOrder> findByVehicleType(Vehicle vehicleType);
    long countByCreatedAtAfter(Timestamp startOfMonthTimestamp);

    @Query("SELECT so FROM ServiceOrder so JOIN FETCH so.vehicleType JOIN FETCH so.service")
    List<ServiceOrder> findAllWithVehicleTypeAndService();
    List<ServiceOrder> findByAssignedTechnician(Technician assignedTechnician);
}