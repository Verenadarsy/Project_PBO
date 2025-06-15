package pbo.autocare.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.math.BigDecimal; 

@Entity
@Table(name = "service_orders")
public class ServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_contact", nullable = false)
    private String customerContact;

    @Column(name = "customer_address", columnDefinition = "TEXT")
    private String customerAddress;

    @Column(name = "vehicle_model_name", nullable = false)
    private String vehicleModelName;

    @ManyToOne
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private Vehicle vehicleType;

    @Column(name = "license_plate", nullable = false, length = 20)
    private String licensePlate;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceItem service;

    @Column(name = "final_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @Column(name = "selected_duration_days") 
    private Integer selectedDurationDays;

    public Integer getSelectedDurationDays() {
        return selectedDurationDays;
    }

    public void setSelectedDurationDays(Integer selectedDurationDays) {
        this.selectedDurationDays = selectedDurationDays;
    }

    @Column(name = "service_name", nullable = false, length = 255) 
    private String serviceName; 
    
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "order_notes", columnDefinition = "TEXT")
    private String orderNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    public enum OrderStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    }

     @ManyToOne
    @JoinColumn(name = "assigned_technician_id") 
    private Technician assignedTechnician;

    public Technician getAssignedTechnician() {
        return assignedTechnician;
    }

    public void setAssignedTechnician(Technician assignedTechnician) {
        this.assignedTechnician = assignedTechnician;
     }

    public ServiceOrder() {
        this.orderStatus = OrderStatus.PENDING;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public ServiceOrder(User user, String customerName, String customerContact, String customerAddress,
                        String vehicleModelName, Vehicle vehicleType, String licensePlate,
                        ServiceItem service, BigDecimal finalPrice, String orderNotes) { 
        this(null, user, customerName, customerContact, customerAddress,
             vehicleModelName, vehicleType, licensePlate, service, finalPrice,
             OrderStatus.PENDING, orderNotes, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
    }

    public ServiceOrder(Long id, User user, String customerName, String customerContact, String customerAddress,
                        String vehicleModelName, Vehicle vehicleType, String licensePlate,
                        ServiceItem service, BigDecimal finalPrice, OrderStatus orderStatus, String orderNotes, 
                        Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.user = user;
        this.customerName = customerName;
        this.customerContact = customerContact;
        this.customerAddress = customerAddress;
        this.vehicleModelName = vehicleModelName;
        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
        this.service = service;
        this.finalPrice = finalPrice;
        this.orderStatus = orderStatus;
        this.orderNotes = orderNotes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerContact() { return customerContact; }
    public void setCustomerContact(String customerContact) { this.customerContact = customerContact; }
    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }
    public String getVehicleModelName() { return vehicleModelName; }
    public void setVehicleModelName(String vehicleModelName) { this.vehicleModelName = vehicleModelName; }
    public Vehicle getVehicleType() { return vehicleType; }
    public void setVehicleType(Vehicle vehicleType) { this.vehicleType = vehicleType; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public ServiceItem getService() { return service; }
    public void setService(ServiceItem service) { this.service = service; }
    public BigDecimal getFinalPrice() { return finalPrice; } 
    public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }
    public String getOrderNotes() { return orderNotes; }
    public void setOrderNotes(String orderNotes) { this.orderNotes = orderNotes; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}