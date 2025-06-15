package pbo.autocare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 
import pbo.autocare.model.Customer;
import pbo.autocare.repository.CustomerRepository;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder; 

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<Customer> findAllCustomers() {

        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> findCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Autowired
    private PasswordEncoder passwordEncoder; 

    @Override
    @Transactional
    public Customer saveCustomer(Customer customer) {
    if (customer.getId() == null) {

        customer.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        return customerRepository.save(customer);
    } else {
        Optional<Customer> existing = customerRepository.findById(customer.getId());
        if (existing.isPresent()) {
            Customer old = existing.get();

            old.setUsername(customer.getUsername());
            old.setEmail(customer.getEmail());
            old.setFullName(customer.getFullName());
            old.setPhoneNumber(customer.getPhoneNumber());

            if (customer.getPassword() != null && !customer.getPassword().isEmpty()) {
                old.setPassword(passwordEncoder.encode(customer.getPassword()));
            }

                return customerRepository.save(old);
            } else {
                throw new RuntimeException("Customer tidak ditemukan untuk diedit");
            }
        }
    }

    @Override
    @Transactional
    public void deleteCustomerById(Long id) {
        customerRepository.deleteById(id);
    }
}