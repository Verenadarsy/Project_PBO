package pbo.autocare.service;

import pbo.autocare.model.Vehicle;
import pbo.autocare.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service 
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    @Transactional
    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @Override 
    @Transactional(readOnly = true)
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override 
    @Transactional(readOnly = true)
    public Optional<Vehicle> getVehicleById(Integer id) {
        return vehicleRepository.findById(id);
    }

    @Override 
    @Transactional
    public Vehicle updateVehicle(Integer id, Vehicle updatedVehicleDetails) {
        return vehicleRepository.findById(id)
                .map(existingVehicle -> {
                    existingVehicle.setVehicleType(updatedVehicleDetails.getVehicleType());
                    existingVehicle.setDescription(updatedVehicleDetails.getDescription());
                    existingVehicle.setBaseServiceCostMultiplier(updatedVehicleDetails.getBaseServiceCostMultiplier());
                    return vehicleRepository.save(existingVehicle);
                }).orElseThrow(() -> new RuntimeException("Vehicle tidak ditemukan dengan ID: " + id));
    }

    @Override 
    @Transactional
    public void deleteVehicle(Integer id) {
        vehicleRepository.deleteById(id);
    }
}