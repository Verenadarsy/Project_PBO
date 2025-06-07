// src/main/java/pbo/autocare/service/CustomerServiceImpl.java
package pbo.autocare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Penting untuk operasi tulis
import pbo.autocare.model.Customer;
import pbo.autocare.repository.CustomerRepository;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder; // Untuk hashing password

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<Customer> findAllCustomers() {
        // Karena Customer adalah subclass dari User dengan DiscriminatorValue "CUSTOMER"
        // findAll() dari JpaRepository akan secara otomatis hanya mengambil Customer.
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> findCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject PasswordEncoder Anda

    @Override
    @Transactional
    public Customer saveCustomer(Customer customer) {
    if (customer.getId() == null) {
        // CREATE baru
        customer.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        return customerRepository.save(customer);
    } else {
        // UPDATE existing
        Optional<Customer> existing = customerRepository.findById(customer.getId());
        if (existing.isPresent()) {
            Customer old = existing.get();

            // Update field satu per satu
            old.setUsername(customer.getUsername());
            old.setEmail(customer.getEmail());
            old.setFullName(customer.getFullName());
            old.setPhoneNumber(customer.getPhoneNumber());

            // Update password hanya jika user mengisinya
            if (customer.getPassword() != null && !customer.getPassword().isEmpty()) {
                old.setPassword(passwordEncoder.encode(customer.getPassword()));
            }

            // updatedAt akan diisi otomatis oleh Auditing (karena kamu pakai @LastModifiedDate)
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