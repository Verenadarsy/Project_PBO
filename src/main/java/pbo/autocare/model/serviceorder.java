// src/main/java/pbo/autocare/model/ServiceOrder.java
package pbo.autocare.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "service_orders")
public class ServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // Relasi Many-to-One ke User
    @JoinColumn(name = "user_id") // Kolom FK di tabel service_orders
    private User user; // Bisa berupa User, Admin, Customer, dll. (polimorfik)

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
    private Vehicle vehicleType; // Relasi ke Vehicle

    @Column(name = "license_plate", nullable = false, length = 20)
    private String licensePlate;

    @ManyToOne
    @JoinColumn(name = "service_name", referencedColumnName = "service_name") // kolom FK = service_name, referensi ke service_items.name
    private ServiceItem service;

    @Column(name = "final_price", nullable = false)
    private double finalPrice; // Gunakan BigDecimal untuk mata uang

    @Enumerated(EnumType.STRING) // Simpan ENUM sebagai String di DB
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus; // Gunakan Enum untuk status

    @Column(name = "order_notes", columnDefinition = "TEXT")
    private String orderNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    // Enum untuk OrderStatus
    public enum OrderStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    }

    // Default constructor diperlukan oleh JPA dan PENTING untuk Spring Forms!
    public ServiceOrder() {
        // Inisialisasi default properti ServiceOrder
        this.orderStatus = OrderStatus.PENDING;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());

        // --- INI BARIS KRITIS YANG HILANG ATAU BELUM ANDA TERAPKAN ---
        // Karena User, Vehicle, dan ServiceItem adalah kelas abstract,
        // Anda harus menginisialisasikannya dengan *instance* dari *sub-kelas konkret* mereka.
        // Ganti 'Customer()', 'Vehicle()', dan 'GeneralService()' dengan nama sub-kelas konkret Anda yang sesuai.

        // this.user = new Customer(); // <-- GANTI 'Customer' dengan sub-kelas konkret dari User yang Anda miliki
        //                             // Contoh: new Admin(), new Staff(), new Technician(), atau new Customer()

        // this.vehicleType = new Vehicle(); // <-- GANTI 'Vehicle' dengan sub-kelas konkret dari Vehicle jika Vehicle abstract.
        //                                  // Jika Vehicle *bukan* abstract, maka 'new Vehicle()' sudah benar.

        // this.service = new Generalservice(); // <-- GANTI 'GeneralService' dengan sub-kelas konkret dari ServiceItem yang Anda miliki.
                                            // Contoh: new SpecializedService() atau new GeneralService()
    }

    // Constructor untuk membuat Order baru (tanpa ID, ID akan di-generate DB)
    public ServiceOrder(User user, String customerName, String customerContact, String customerAddress,
                        String vehicleModelName, Vehicle vehicleType, String licensePlate,
                        ServiceItem service, double finalPrice, String orderNotes) {
                        this(null, user, customerName, customerContact, customerAddress,
                            vehicleModelName, vehicleType, licensePlate, service, finalPrice,
                            OrderStatus.PENDING, orderNotes, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
                        }

    // Constructor lengkap (untuk memuat dari DB)
    public ServiceOrder(Long id, User user, String customerName, String customerContact, String customerAddress,
                        String vehicleModelName, Vehicle vehicleType, String licensePlate,
                        ServiceItem service, double finalPrice, OrderStatus orderStatus, String orderNotes,
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

    // Getters dan Setters
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
    public double getFinalPrice() { return finalPrice; }
    public void setFinalPrice(double finalPrice) { this.finalPrice = finalPrice; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }
    public String getOrderNotes() { return orderNotes; }
    public void setOrderNotes(String orderNotes) { this.orderNotes = orderNotes; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}