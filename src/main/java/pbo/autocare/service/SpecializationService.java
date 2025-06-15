package pbo.autocare.service;

import pbo.autocare.model.Specialization;
import pbo.autocare.repository.SpecializationRepository;
import pbo.autocare.dto.SpecializationListDTO; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SpecializationService {

    private final SpecializationRepository specializationRepository;

    public SpecializationService(SpecializationRepository specializationRepository) {
        this.specializationRepository = specializationRepository;
    }

    @Transactional
    public Specialization saveSpecialization(Specialization specialization) {
        return specializationRepository.save(specialization);
    }

    @Transactional(readOnly = true)
    public List<SpecializationListDTO> getAllSpecializationsWithCount() {
        return specializationRepository.findAllSpecializationsWithTechnicianCount();
    }

    @Transactional(readOnly = true)
    public Optional<SpecializationListDTO> getSpecializationWithCountById(Long id) {
        return specializationRepository.findSpecializationWithTechnicianCountById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Specialization> getSpecializationById(Long id) {
        return specializationRepository.findById(id); 
    }

    @Transactional
    public Specialization updateSpecialization(Long id, Specialization specializationDetails) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spesialisasi tidak ditemukan dengan ID: " + id));

        specialization.setCode(specializationDetails.getCode());
        specialization.setDescription(specializationDetails.getDescription());
        return specializationRepository.save(specialization);
    }

    @Transactional
    public void deleteSpecialization(Long id) {

        Optional<SpecializationListDTO> dto = specializationRepository.findSpecializationWithTechnicianCountById(id);
        if (dto.isPresent() && dto.get().getTechnicianCount() > 0) {
            throw new IllegalStateException("Tidak dapat menghapus spesialisasi ini. Masih ada " +
                                            dto.get().getTechnicianCount() + " teknisi yang terdaftar di spesialisasi ini.");
        }

        specializationRepository.deleteById(id);
    }
    
}
