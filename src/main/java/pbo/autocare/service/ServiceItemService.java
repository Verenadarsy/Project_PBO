package pbo.autocare.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import pbo.autocare.model.ServiceItem;
import pbo.autocare.repository.ServiceItemRepository;

public class ServiceItemService {

    @Autowired
    private ServiceItemRepository serviceItemRepository;

    public List<ServiceItem> getAllServiceItems() {
        return serviceItemRepository.findAll(); // This fetches all ServiceItem entities from the database
    }
    
}
