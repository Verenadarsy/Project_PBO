// src/main/java/pbo/autocare/repository/ServiceRepository.java
package pbo.autocare.repository;

import pbo.autocare.model.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceItem, Long> {

    // Contoh method kustom:
    // Mencari semua layanan berdasarkan kategori layanan (service_category)
    List<ServiceItem> findByServiceCategory(String serviceCategory);

    // Contoh lain:
    // Mencari semua layanan berdasarkan tipe (General atau Special) // Ini akan bekerja karena service_type adalah discriminator column

    // Mencari layanan yang harganya di atas nilai tertentu
    List<ServiceItem> findByBasePriceGreaterThan(double price);
}