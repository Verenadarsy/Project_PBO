package pbo.autocare.model;

import java.math.BigDecimal;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("Special")
public class SpecializedService extends ServiceItem {

    public SpecializedService() {
        super(); 
    }

    public SpecializedService(Long id, String serviceName, String serviceCategory, BigDecimal basePrice, 
                              int specialDurationDaysMin, int specialDurationDaysMax,
                              Specialization requiredSpecialization) {
        super(id, serviceName, serviceCategory, basePrice,
              0, // generalDurationDaysMin = 0 untuk SpecializedService
              0, // generalDurationDaysMax = 0 untuk SpecializedService
              specialDurationDaysMin, specialDurationDaysMax,
              requiredSpecialization);
    }

    public SpecializedService(String serviceName, String serviceCategory, BigDecimal basePrice,
                              int specialDurationDaysMin, int specialDurationDaysMax,
                              Specialization requiredSpecialization) {
        super(null, serviceName, serviceCategory, basePrice,
              0,
              0,
              specialDurationDaysMin, specialDurationDaysMax,
              requiredSpecialization);
    }

    @Override
    public BigDecimal calculateFinalPrice(Vehicle vehicle) {

        if (this.getBasePrice() == null || vehicle == null || vehicle.getBaseServiceCostMultiplier() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal calculatedPrice = this.getBasePrice().multiply(vehicle.getBaseServiceCostMultiplier());

        return calculatedPrice;
    }

    @Override
    public String getEstimatedDuration() {
        return this.getSpecialDurationDaysMin() + " - " + this.getSpecialDurationDaysMax() + " hari";
    }

     @Override
    public String getServiceType() {
        return "Spesialis"; 
    }
}