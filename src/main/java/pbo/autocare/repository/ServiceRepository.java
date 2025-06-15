package pbo.autocare.repository;

import pbo.autocare.model.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceItem, Long> {

    List<ServiceItem> findByServiceCategory(String serviceCategory);
    List<ServiceItem> findByBasePriceGreaterThan(double price);
}