package pbo.autocare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pbo.autocare.model.ServiceOrder;
import pbo.autocare.model.User;
import pbo.autocare.model.Vehicle;
import pbo.autocare.model.ServiceItem;
import pbo.autocare.repository.ServiceOrderRepository;
import pbo.autocare.repository.UserRepository;
import pbo.autocare.repository.VehicleRepository;
import pbo.autocare.repository.ServiceRepository;

import java.util.List;
import java.util.Optional;
import java.sql.Timestamp;

@Service
public class ServiceOrderServiceImpl implements ServiceOrderService { 

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

   @Autowired
    private ServiceRepository serviceRepository;

    @Override
    public List<ServiceItem> getAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    public ServiceOrder saveServiceOrder(ServiceOrder serviceOrder) {
        if (serviceOrder.getId() == null) {
            serviceOrder.setOrderStatus(ServiceOrder.OrderStatus.PENDING);
            serviceOrder.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }
        serviceOrder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return serviceOrderRepository.save(serviceOrder);
    }

    @Override
    public Optional<ServiceOrder> findById(Long id) {
        return serviceOrderRepository.findById(id);
    }

    @Override
    public List<ServiceOrder> findAll() {
        return serviceOrderRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        serviceOrderRepository.deleteById(id);
    }
}