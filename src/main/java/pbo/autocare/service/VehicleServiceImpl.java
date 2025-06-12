package pbo.autocare.service;

import pbo.autocare.model.Vehicle;
import pbo.autocare.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service // Tetap ada @Service di kelas implementasi
public class VehicleServiceImpl implements VehicleService { // <-- TAMBAHKAN 'implements VehicleService'

    private final VehicleRepository vehicleRepository;

    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override // <-- Tambahkan @Override untuk memastikan implementasi dari antarmuka
    @Transactional
    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @Override // <-- Tambahkan @Override
    @Transactional(readOnly = true)
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override // <-- Tambahkan @Override
    @Transactional(readOnly = true)
    public Optional<Vehicle> getVehicleById(Integer id) {
        return vehicleRepository.findById(id);
    }

    @Override // <-- Tambahkan @Override
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

    @Override // <-- Tambahkan @Override
    @Transactional
    public void deleteVehicle(Integer id) {
        vehicleRepository.deleteById(id);
    }
}