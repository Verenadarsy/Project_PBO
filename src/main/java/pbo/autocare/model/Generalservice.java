package pbo.autocare.model;

import java.math.BigDecimal;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("General")
public class Generalservice extends ServiceItem {

    public Generalservice() {
        super();
    }

    public Generalservice(Long id, String serviceName, String serviceCategory, BigDecimal basePrice,
                          int generalDurationDaysMin, int generalDurationDaysMax,
                          Specialization requiredSpecialization) {
        super(id, serviceName, serviceCategory, basePrice,
              generalDurationDaysMin, generalDurationDaysMax,
              0, // specialDurationDaysMin
              0, // specialDurationDaysMax
              requiredSpecialization);
    }

    public Generalservice(String serviceName, String serviceCategory, BigDecimal basePrice,
                          int generalDurationDaysMin, int generalDurationDaysMax,
                          Specialization requiredSpecialization) {
        super(null, serviceName, serviceCategory, basePrice,
              generalDurationDaysMin, generalDurationDaysMax,
              0,
              0,
              requiredSpecialization);
    }

    @Override
    public BigDecimal calculateFinalPrice(Vehicle vehicle) {

        if (this.getBasePrice() == null || vehicle == null || vehicle.getBaseServiceCostMultiplier() == null) {
            return BigDecimal.ZERO; 
        }
        return this.getBasePrice().multiply(vehicle.getBaseServiceCostMultiplier());
    }

    @Override
    public String getEstimatedDuration() {
        return this.getGeneralDurationDaysMin() + " - " + this.getGeneralDurationDaysMax() + " hari";
    }

    @Override
    public String getServiceType() {
        return "General"; 
    }
}