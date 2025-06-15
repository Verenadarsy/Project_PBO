package pbo.autocare.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;

import pbo.autocare.dto.CustomerFormDTO;
import pbo.autocare.model.Customer;      
import pbo.autocare.model.User;         

public interface UserService extends UserDetailsService {

    Customer registerNewCustomer(Customer customer);
    void createSuperUser(String username, String rawPassword, String userType);
    Optional<User> findUserByUsername(String username); 
    Optional<User> findUserEntityByUsername(String username);
    boolean existsByUsername(String username);
    List<User> getAllUsers();
    List<User> getAllStaff();
    List<User> getAllTechnicians();
    List<Customer> getAllCustomers();
    Customer saveNewCustomer(CustomerFormDTO customerDTO);
    Optional<Customer> getCustomerById(Long id);
    Customer updateCustomer(Long id, CustomerFormDTO customerDTO);
    void deleteCustomer(Long id);

    long countCustomers();
    long countTechnicians();
    long countStaff();
}