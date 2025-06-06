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

    public SpecializedService(Long id, String serviceName, String serviceCategory, BigDecimal basePrice, // <--- UBAH: Long id, basePrice tetap double
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
        // Logika perhitungan untuk Specialized Service
        // Bisa jadi ada faktor lain, misalnya diskon atau biaya tambahan
        // Untuk contoh ini, kita pakai logika yang sama seperti GeneralService dulu
        if (this.getBasePrice() == null || vehicle == null || vehicle.getBaseServiceCostMultiplier() == null) {
            return BigDecimal.ZERO; // Atau throw exception jika data tidak lengkap
        }
        // Contoh: Mungkin SpecializedService memiliki biaya tambahan atau diskon khusus
        BigDecimal calculatedPrice = this.getBasePrice().multiply(vehicle.getBaseServiceCostMultiplier());
        // Jika ada logic khusus, tambahkan di sini. Misalnya:
        // if (this.getRequiredSpecialization() != null && this.getRequiredSpecialization().getCode().equals("ADVANCED")) {
        //     calculatedPrice = calculatedPrice.add(new BigDecimal("50000")); // Tambah biaya khusus
        // }
        return calculatedPrice;
    }

    @Override
    public String getEstimatedDuration() {
        return this.getSpecialDurationDaysMin() + " - " + this.getSpecialDurationDaysMax() + " hari";
    }

     @Override
    public String getServiceType() {
        return "Spesialis"; // Mengembalikan nilai discriminator untuk SpecializedService
    }
}