// src/main/java/pbo/autocare/model/Vehicle.java
package pbo.autocare.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "Vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "vehicle_type", unique = true, nullable = false, length = 50)
    private String vehicleType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // HAPUS precision dan scale dari sini!
    @Column(name = "base_service_cost_multiplier") // <--- Perbaikan di sini
    private double baseServiceCostMultiplier;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public Vehicle() {} // Konstruktor default

    public Vehicle(int id, String vehicleType, String description, double baseServiceCostMultiplier) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.description = description;
        this.baseServiceCostMultiplier = baseServiceCostMultiplier;
    }

    public Vehicle(String vehicleType, String description, double baseServiceCostMultiplier) {
        this(0, vehicleType, description, baseServiceCostMultiplier);
    }

    // Getters dan Setters...
    public long  getId() { return id; }
    public String getVehicleType() { return vehicleType; }
    public String getDescription() { return description; }
    public double getBaseServiceCostMultiplier() { return baseServiceCostMultiplier; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }

    public void setId(int id) { this.id = id; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public void setDescription(String description) { this.description = description; }
    public void setBaseServiceCostMultiplier(double baseServiceCostMultiplier) { this.baseServiceCostMultiplier = baseServiceCostMultiplier; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Vehicle{" +
               "id=" + id +
               ", vehicleType='" + vehicleType + '\'' +
               ", description='" + description + '\'' +
               ", baseServiceCostMultiplier=" + baseServiceCostMultiplier +
               '}';
    }
}