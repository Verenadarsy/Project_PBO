package pbo.autocare.repository;

import pbo.autocare.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
//import java.util.Optional;

// Anda juga bisa membuat repository spesifik jika ingin query khusus untuk Customer
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // Optional: Anda bisa menambahkan method khusus untuk customer jika diperlukan
    // Contoh: List<Customer> findByEmail(String email);
}

