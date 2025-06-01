package pbo.autocare.service;

import pbo.autocare.model.User;
import pbo.autocare.model.Customer;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Customer registerNewCustomer(Customer customer);
    Optional<User> findUserByUsername(String username);
    boolean existsByUsername(String username);
    List<User> getAllStaff();
    List<User> getAllTechnicians();
}