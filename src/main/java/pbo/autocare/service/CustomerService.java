package pbo.autocare.service;

import pbo.autocare.model.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<Customer> findAllCustomers();
    Optional<Customer> findCustomerById(Long id);
    Customer saveCustomer(Customer customer);
    void deleteCustomerById(Long id);
}