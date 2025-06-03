package pbo.autocare.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "services")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "service_type", discriminatorType = DiscriminatorType.STRING)
public abstract class ServiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id; 

    @Column(name = "service_name", unique = true, nullable = false)
    protected String serviceName;

    @Column(name = "service_category")
    protected String serviceCategory;

    @Column(name = "base_price")
    protected double basePrice; 

    @Column(name = "general_duration_days_min")
    protected int generalDurationDaysMin;

    @Column(name = "general_duration_days_max")
    protected int generalDurationDaysMax;

    @Column(name = "special_duration_days_min")
    protected int specialDurationDaysMin;

    @Column(name = "special_duration_days_max")
    protected int specialDurationDaysMax;

    @ManyToOne
    @JoinColumn(name = "required_specialization_id")
    private Specialization requiredSpecialization;

    @Column(name = "created_at")
    protected Timestamp createdAt;

    @Column(name = "updated_at")
    protected Timestamp updatedAt;

    public ServiceItem() {}

    public ServiceItem(Long id, String serviceName, String serviceCategory, double basePrice, // <--- UBAH double ke BigDecimal
                   int generalDurationDaysMin, int generalDurationDaysMax,
                   int specialDurationDaysMin, int specialDurationDaysMax,
                   Specialization requiredSpecialization) {
        this.id = id;
        this.serviceName = serviceName;
        this.serviceCategory = serviceCategory;
        this.basePrice = basePrice;
        this.generalDurationDaysMin = generalDurationDaysMin;
        this.generalDurationDaysMax = generalDurationDaysMax;
        this.specialDurationDaysMin = specialDurationDaysMin;
        this.specialDurationDaysMax = specialDurationDaysMax;
        this.requiredSpecialization = requiredSpecialization;
    }

    public abstract double calculateFinalPrice(Vehicle vehicle); 
    public abstract String getEstimatedDuration();

    public Long getId() { return id; } 
    public void setId(Long id) { this.id = id; } 
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getServiceCategory() { return serviceCategory; }
    public void setServiceCategory(String serviceCategory) { this.serviceCategory = serviceCategory; }
    public double getBasePrice() { return basePrice; } 
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public int getGeneralDurationDaysMin() { return generalDurationDaysMin; }
    public void setGeneralDurationDaysMin(int generalDurationDaysMin) { this.generalDurationDaysMin = generalDurationDaysMin; }
    public int getGeneralDurationDaysMax() { return generalDurationDaysMax; }
    public void setGeneralDurationDaysMax(int generalDurationDaysMax) { this.generalDurationDaysMax = generalDurationDaysMax; }
    public int getSpecialDurationDaysMin() { return specialDurationDaysMin; }
    public void setSpecialDurationDaysMin(int specialDurationDaysMin) { this.specialDurationDaysMin = specialDurationDaysMin; }
    public int getSpecialDurationDaysMax() { return specialDurationDaysMax; }
    public void setSpecialDurationDaysMax(int specialDurationDaysMax) { this.specialDurationDaysMax = specialDurationDaysMax; }

    public Specialization getRequiredSpecialization() { return requiredSpecialization; }
    public void setRequiredSpecialization(Specialization requiredSpecialization) { this.requiredSpecialization = requiredSpecialization; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; } 

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", serviceName='" + serviceName + '\'' +
                ", serviceCategory='" + serviceCategory + '\'' +
                ", basePrice=" + basePrice +
                ", requiredSpecialization=" + (requiredSpecialization != null ? requiredSpecialization.getCode() : "N/A") +
                '}';
    }
}