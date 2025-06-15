package pbo.autocare.repository;

import pbo.autocare.model.Vehicle; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; 

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {


    Optional<Vehicle> findByVehicleType(String vehicleType);
    
}