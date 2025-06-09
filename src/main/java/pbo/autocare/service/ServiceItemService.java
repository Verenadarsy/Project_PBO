package pbo.autocare.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pbo.autocare.model.ServiceItem;
import pbo.autocare.repository.ServiceItemRepository;

@Service
public class ServiceItemService {
    @Autowired
    private ServiceItemRepository serviceItemRepository;

    public List<ServiceItem> getAllServiceItems() {
        return serviceItemRepository.findAll(); // Will return a list containing both GeneralService and SpecializedService objects
    }

    public Optional<ServiceItem> getServiceItemById(Long id) {
        return serviceItemRepository.findById(id);
    }

    // This method will now accept and save either a GeneralService or SpecializedService instance
    public ServiceItem createServiceItem(ServiceItem serviceItem) {
        // You can add logic here specific to setting creation timestamps if not using @PrePersist/@PreUpdate
        // serviceItem.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        // serviceItem.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return serviceItemRepository.save(serviceItem);
    }

    // The update method will also work polymorphically
    public ServiceItem updateServiceItem(Long id, ServiceItem updatedServiceItem) {
        return serviceItemRepository.findById(id)
                .map(existingItem -> {
                    // Update common fields
                    existingItem.setServiceName(updatedServiceItem.getServiceName());
                    existingItem.setServiceCategory(updatedServiceItem.getServiceCategory());
                    existingItem.setBasePrice(updatedServiceItem.getBasePrice());
                    existingItem.setGeneralDurationDaysMin(updatedServiceItem.getGeneralDurationDaysMin());
                    existingItem.setGeneralDurationDaysMax(updatedServiceItem.getGeneralDurationDaysMax());
                    existingItem.setSpecialDurationDaysMin(updatedServiceItem.getSpecialDurationDaysMin());
                    existingItem.setSpecialDurationDaysMax(updatedServiceItem.getSpecialDurationDaysMax());
                    existingItem.setRequiredSpecialization(updatedServiceItem.getRequiredSpecialization());
                    // existingItem.setUpdatedAt(new Timestamp(System.currentTimeMillis())); // If not using @PreUpdate

                    // If you need to update subclass-specific fields, you'd need to cast:
                    // if (existingItem instanceof SpecializedService && updatedServiceItem instanceof SpecializedService) {
                    //     SpecializedService existingSpecialized = (SpecializedService) existingItem;
                    //     SpecializedService updatedSpecialized = (SpecializedService) updatedServiceItem;
                    //     // existingSpecialized.setSomeSpecialField(updatedSpecialized.getSomeSpecialField());
                    // }

                    return serviceItemRepository.save(existingItem);
                }).orElseThrow(() -> new RuntimeException("Service Item not found with id " + id));
    }

    public void deleteServiceItem(Long id) {
        serviceItemRepository.deleteById(id);
    }
}