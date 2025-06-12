package pbo.autocare.service;

import pbo.autocare.model.Specialization;
import pbo.autocare.repository.SpecializationRepository;
import pbo.autocare.dto.SpecializationListDTO; // Import DTO baru
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SpecializationService {

    private final SpecializationRepository specializationRepository;

    @Autowired
    public SpecializationService(SpecializationRepository specializationRepository) {
        this.specializationRepository = specializationRepository;
    }

    // Create (Buat)
    @Transactional
    public Specialization saveSpecialization(Specialization specialization) {
        return specializationRepository.save(specialization);
    }

    // Read All (Baca Semua) - Menggunakan DTO untuk mendapatkan jumlah teknisi
    @Transactional(readOnly = true)
    public List<SpecializationListDTO> getAllSpecializationsWithCount() {
        return specializationRepository.findAllSpecializationsWithTechnicianCount();
    }

    // Read By ID (Baca Berdasarkan ID) - Menggunakan DTO untuk mendapatkan jumlah teknisi
    @Transactional(readOnly = true)
    public Optional<SpecializationListDTO> getSpecializationWithCountById(Long id) {
        return specializationRepository.findSpecializationWithTechnicianCountById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Specialization> getSpecializationById(Long id) {
        return specializationRepository.findById(id); // Metode bawaan JpaRepository
    }

    // Update (Perbarui) - Tetap menggunakan Specialization entity
    @Transactional
    public Specialization updateSpecialization(Long id, Specialization specializationDetails) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spesialisasi tidak ditemukan dengan ID: " + id));

        specialization.setCode(specializationDetails.getCode());
        specialization.setDescription(specializationDetails.getDescription());
        return specializationRepository.save(specialization);
    }

    // Delete (Hapus)
    @Transactional
    public void deleteSpecialization(Long id) {
        // Kita perlu mengecek jumlah teknisi secara terpisah karena Specialization model tidak lagi punya koleksi
        Optional<SpecializationListDTO> dto = specializationRepository.findSpecializationWithTechnicianCountById(id);
        if (dto.isPresent() && dto.get().getTechnicianCount() > 0) {
            throw new IllegalStateException("Tidak dapat menghapus spesialisasi ini. Masih ada " +
                                            dto.get().getTechnicianCount() + " teknisi yang terdaftar di spesialisasi ini.");
        }

        specializationRepository.deleteById(id);
    }
    
}
