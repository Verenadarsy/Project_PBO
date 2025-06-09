package pbo.autocare.repository;

import pbo.autocare.model.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long> {
    // Mencari teknisi berdasarkan kode spesialisasi mereka
    // Contoh: findBySpecialization_Code("AC") akan mencari teknisi dengan spesialisasi AC
    List<Technician> findBySpecialization_Code(String specializationCode);

    // Anda juga bisa mencari berdasarkan description jika perlu
    // List<Technician> findBySpecialization_Description(String specializationDescription);
    // Mencari teknisi berdasarkan nama{
    
}
