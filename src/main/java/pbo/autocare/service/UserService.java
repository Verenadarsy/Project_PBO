package pbo.autocare.service;

import pbo.autocare.model.User; // Import User
import pbo.autocare.model.Customer;
import java.util.Optional; // Import ini

public interface UserService {
    Customer registerNewCustomer(Customer customer);
    Optional<User> findUserByUsername(String username); // Tambahkan ini
}