// src/main/java/pbo/autocare/model/Vehicle.java (Contoh)
package pbo.autocare.model;

import jakarta.persistence.*;
import java.math.BigDecimal; // Tambahkan ini

@Entity
@Table(name = "vehicles") // Asumsi nama tabel adalah 'vehicles'
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_type", nullable = false, unique = true)
    private String vehicleType; // Contoh: Sedan, Hatchback, MPV / Minibus

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Menggunakan BigDecimal untuk multiplier agar konsisten dengan basePrice
    @Column(name = "base_service_cost_multiplier", nullable = false, precision = 4, scale = 2)
    private BigDecimal baseServiceCostMultiplier; // Akan disimpan sebagai decimal di DB

    // Constructors
    public Vehicle() {}

    public Vehicle(String vehicleType, String description, BigDecimal baseServiceCostMultiplier) {
        this.vehicleType = vehicleType;
        this.description = description;
        this.baseServiceCostMultiplier = baseServiceCostMultiplier;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getBaseServiceCostMultiplier() { return baseServiceCostMultiplier; }
    public void setBaseServiceCostMultiplier(BigDecimal baseServiceCostMultiplier) { this.baseServiceCostMultiplier = baseServiceCostMultiplier; }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", vehicleType='" + vehicleType + '\'' +
                ", baseServiceCostMultiplier=" + baseServiceCostMultiplier +
                '}';
    }
}