// src/main/java/pbo/autocare/repository/SpecializationRepository.java
package pbo.autocare.repository;

import pbo.autocare.dto.SpecializationListDTO;
import pbo.autocare.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    // Tambahkan method kustom jika perlu, misalnya mencari berdasarkan code
    Optional<Specialization> findByCode(String code);
    
    @Query("SELECT new pbo.autocare.dto.SpecializationListDTO(s.id, s.code, s.description, COUNT(t.id)) " +
           "FROM Specialization s LEFT JOIN Technician t ON s.id = t.specialization.id GROUP BY s.id, s.code, s.description")
    List<SpecializationListDTO> findAllSpecializationsWithTechnicianCount();

    // Untuk findById, kita juga bisa menggunakan DTO Projection
    @Query("SELECT new pbo.autocare.dto.SpecializationListDTO(s.id, s.code, s.description, COUNT(t.id)) " +
           "FROM Specialization s LEFT JOIN Technician t ON s.id = t.specialization.id " +
           "WHERE s.id = :id GROUP BY s.id, s.code, s.description")
    Optional<SpecializationListDTO> findSpecializationWithTechnicianCountById(Long id);
}