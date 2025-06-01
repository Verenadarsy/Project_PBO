package pbo.autocare.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("General")
public class Generalservice extends ServiceItem {

    public Generalservice() {
        super();
    }

    public Generalservice(Long id, String serviceName, String serviceCategory, double basePrice,
                          int generalDurationDaysMin, int generalDurationDaysMax,
                          Specialization requiredSpecialization) {
        super(id, serviceName, serviceCategory, basePrice,
              generalDurationDaysMin, generalDurationDaysMax,
              0, // specialDurationDaysMin
              0, // specialDurationDaysMax
              requiredSpecialization);
    }

    public Generalservice(String serviceName, String serviceCategory, double basePrice,
                          int generalDurationDaysMin, int generalDurationDaysMax,
                          Specialization requiredSpecialization) {
        super(null, serviceName, serviceCategory, basePrice,
              generalDurationDaysMin, generalDurationDaysMax,
              0,
              0,
              requiredSpecialization);
    }

    @Override
    public double calculateFinalPrice(Vehicle vehicle) {
        return this.getBasePrice() * vehicle.getBaseServiceCostMultiplier();
    }

    @Override
    public String getEstimatedDuration() {
        return this.getGeneralDurationDaysMin() + " - " + this.getGeneralDurationDaysMax() + " hari";
    }
}