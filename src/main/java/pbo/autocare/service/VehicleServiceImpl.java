// src/main/java/pbo/autocare.service/VehicleServiceImpl.java
package pbo.autocare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pbo.autocare.model.Vehicle;
import pbo.autocare.repository.VehicleRepository; // You'll need this too

import java.util.List;
import java.util.Optional;

@Service // <--- THIS IS THE CRUCIAL ANNOTATION!
public class VehicleServiceImpl implements VehicleService { // Implement the interface

    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public Optional<Vehicle> getVehicleById(Integer id) {
        return vehicleRepository.findById(id);
    }

    @Override
    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    // @Override
    // public Vehicle updateVehicle(Integer id, Vehicle updatedVehicle) {
    //     return vehicleRepository.findById(id)
    //             .map(existingVehicle -> {
    //                 existingVehicle.setName(updatedVehicle.getName());
    //                 // ... set other fields that can be updated
    //                 return vehicleRepository.save(existingVehicle);
    //             }).orElseThrow(() -> new RuntimeException("Vehicle not found with id " + id));
    // }

    @Override
    public void deleteVehicle(Integer id) {
        vehicleRepository.deleteById(id);
    }
}