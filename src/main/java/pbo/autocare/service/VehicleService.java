// src/main/java/pbo/autocare.service/VehicleService.java (Interface)
package pbo.autocare.service;

import pbo.autocare.model.Vehicle; // Assuming you have a Vehicle model
import java.util.List;
import java.util.Optional;

public interface VehicleService {
    List<Vehicle> getAllVehicles();
    Optional<Vehicle> getVehicleById(Integer id);
    Vehicle createVehicle(Vehicle vehicle);
    // Vehicle updateVehicle(Integer id, Vehicle updatedVehicle);
    void deleteVehicle(Integer id);
}

