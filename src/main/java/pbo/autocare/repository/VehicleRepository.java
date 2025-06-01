// src/main/java/pbo/autocare/repository/VehicleRepository.java
package pbo.autocare.repository;

import pbo.autocare.model.Vehicle; // Pastikan package model Vehicle sudah benar
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Untuk findById yang mengembalikan Optional

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    // Integer karena ID di tabel Vehicles kamu adalah INT, bukan Long.
    // Spring Data JPA akan otomatis menyediakan:
    // - save(Vehicle entity)
    // - findById(Integer id)
    // - findAll()
    // - deleteById(Integer id)
    // - dll.

    Optional<Vehicle> findByVehicleType(String vehicleType);
}