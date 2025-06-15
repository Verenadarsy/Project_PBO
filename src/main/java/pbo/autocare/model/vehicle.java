package pbo.autocare.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "vehicle_type", nullable = false, unique = true)
    private String vehicleType; 

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "base_service_cost_multiplier", nullable = false, precision = 4, scale = 2)
    private BigDecimal baseServiceCostMultiplier; 

    public Vehicle() {}

    public Vehicle(String vehicleType, String description, BigDecimal baseServiceCostMultiplier) {
        this.vehicleType = vehicleType;
        this.description = description;
        this.baseServiceCostMultiplier = baseServiceCostMultiplier;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
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