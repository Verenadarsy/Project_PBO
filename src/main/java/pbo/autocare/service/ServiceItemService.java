package pbo.autocare.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 
import pbo.autocare.model.ServiceItem;
import pbo.autocare.model.Specialization;
import pbo.autocare.model.Generalservice; 
import pbo.autocare.model.SpecializedService;
import pbo.autocare.repository.ServiceItemRepository;
import pbo.autocare.repository.SpecializationRepository;

@Service
public class ServiceItemService {

    private final ServiceItemRepository serviceItemRepository;
    private final SpecializationRepository specializationRepository; 

    public ServiceItemService(ServiceItemRepository serviceItemRepository,
                              SpecializationRepository specializationRepository) {
        this.serviceItemRepository = serviceItemRepository;
        this.specializationRepository = specializationRepository;
    }

    @Transactional(readOnly = true) 
    public List<ServiceItem> getAllServiceItems() {
        return serviceItemRepository.findAll();
    }

    @Transactional(readOnly = true) 
    public Optional<ServiceItem> getServiceItemById(Long id) {
        return serviceItemRepository.findById(id);
    }

    @Transactional
    public ServiceItem saveServiceItem(ServiceItem serviceItem) {

        return serviceItemRepository.save(serviceItem);
    }

    @Transactional 
    public ServiceItem updateServiceItem(Long id, ServiceItem updatedServiceItem) {

        ServiceItem existingItem = serviceItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service Item tidak ditemukan dengan ID: " + id));

        existingItem.setServiceName(updatedServiceItem.getServiceName());
        existingItem.setServiceCategory(updatedServiceItem.getServiceCategory());
        existingItem.setBasePrice(updatedServiceItem.getBasePrice());
        existingItem.setRequiredSpecialization(updatedServiceItem.getRequiredSpecialization());

        if (existingItem instanceof Generalservice && updatedServiceItem instanceof Generalservice) {
            Generalservice existingGeneral = (Generalservice) existingItem;
            Generalservice incomingGeneral = (Generalservice) updatedServiceItem;
            existingGeneral.setGeneralDurationDaysMin(incomingGeneral.getGeneralDurationDaysMin());
            existingGeneral.setGeneralDurationDaysMax(incomingGeneral.getGeneralDurationDaysMax());
            existingGeneral.setSpecialDurationDaysMin(0); // Set ke 0 karena ini Generalservice
            existingGeneral.setSpecialDurationDaysMax(0); // Set ke 0 karena ini Generalservice
        } else if (existingItem instanceof SpecializedService && updatedServiceItem instanceof SpecializedService) {
            SpecializedService existingSpecialized = (SpecializedService) existingItem;
            SpecializedService incomingSpecialized = (SpecializedService) updatedServiceItem;
            existingSpecialized.setSpecialDurationDaysMin(incomingSpecialized.getSpecialDurationDaysMin());
            existingSpecialized.setSpecialDurationDaysMax(incomingSpecialized.getSpecialDurationDaysMax());
            existingSpecialized.setGeneralDurationDaysMin(0); // Set ke 0 karena ini SpecializedService
            existingSpecialized.setGeneralDurationDaysMax(0); // Set ke 0 karena ini SpecializedService
        } else {

            throw new IllegalArgumentException("Tidak dapat memperbarui layanan: Tipe layanan tidak sesuai dengan yang tersimpan.");
        }

        return serviceItemRepository.save(existingItem);
    }

    @Transactional 
    public void deleteServiceItem(Long id) {
        serviceItemRepository.deleteById(id);
    }

    @Transactional(readOnly = true) 
    public List<Specialization> getAllSpecializations() {
        return specializationRepository.findAll();
    }

}