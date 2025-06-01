// src/main/java/pbo/autocare/repository/SpecializationRepository.java
package pbo.autocare.repository;

import pbo.autocare.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    // Tambahkan method kustom jika perlu, misalnya mencari berdasarkan code
    Optional<Specialization> findByCode(String code);
}