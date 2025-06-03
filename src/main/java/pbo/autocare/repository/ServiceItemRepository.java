package pbo.autocare.repository;

import pbo.autocare.model.ServiceItem; // Atau GeneralService/SpecializedService jika Anda butuh yang spesifik
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {
}