package pbo.autocare.service;

import pbo.autocare.model.Vehicle; 
import java.util.List;
import java.util.Optional;

public interface VehicleService {

    Vehicle saveVehicle(Vehicle vehicle);

    List<Vehicle> getAllVehicles();

    Optional<Vehicle> getVehicleById(Integer id);

    Vehicle updateVehicle(Integer id, Vehicle updatedVehicleDetails);

    void deleteVehicle(Integer id);
}