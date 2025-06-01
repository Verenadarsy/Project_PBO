package pbo.autocare.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("Special")
public class SpecializedService extends ServiceItem {
    private static final double SPECIAL_PRICE_MARKUP = 1.30;

    public SpecializedService() {
        super(); 
    }

    public SpecializedService(Long id, String serviceName, String serviceCategory, double basePrice, // <--- UBAH: Long id, basePrice tetap double
                              int specialDurationDaysMin, int specialDurationDaysMax,
                              Specialization requiredSpecialization) {
        super(id, serviceName, serviceCategory, basePrice,
              0, // generalDurationDaysMin = 0 untuk SpecializedService
              0, // generalDurationDaysMax = 0 untuk SpecializedService
              specialDurationDaysMin, specialDurationDaysMax,
              requiredSpecialization);
    }

    public SpecializedService(String serviceName, String serviceCategory, double basePrice,
                              int specialDurationDaysMin, int specialDurationDaysMax,
                              Specialization requiredSpecialization) {
        super(null, serviceName, serviceCategory, basePrice,
              0,
              0,
              specialDurationDaysMin, specialDurationDaysMax,
              requiredSpecialization);
    }

    @Override
    public double calculateFinalPrice(Vehicle vehicle) { 
        return (this.getBasePrice() * vehicle.getBaseServiceCostMultiplier()) * SPECIAL_PRICE_MARKUP;
    }

    @Override
    public String getEstimatedDuration() {
        return this.getSpecialDurationDaysMin() + " - " + this.getSpecialDurationDaysMax() + " hari";
    }
}