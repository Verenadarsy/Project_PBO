// src/main/java/pbo/autocare/repository/TransactionRepository.java
package pbo.autocare.repository;

import pbo.autocare.model.Transaction;
import pbo.autocare.model.Transaction.TransactionStatus; // Import enum TransactionStatus
import pbo.autocare.model.ServiceOrder; // Untuk mencari transaksi berdasarkan order
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Spring Data JPA akan menyediakan metode CRUD dasar untuk Transaction

    // --- Contoh Metode Kustom Berdasarkan Kebutuhanmu ---

    // 1. Mencari transaksi berdasarkan ServiceOrder yang terkait (penting untuk relasi One-to-One)
    Optional<Transaction> findByServiceOrder(ServiceOrder serviceOrder);

    // Atau, jika kamu hanya punya order ID:
    Optional<Transaction> findByServiceOrderId(Long serviceOrderId); // Sesuai nama kolom di DB

    // 2. Mencari transaksi berdasarkan status transaksi
    List<Transaction> findByTransactionStatus(TransactionStatus transactionStatus);

    // 3. Mencari transaksi berdasarkan metode pembayaran
    List<Transaction> findByPaymentMethod(String paymentMethod);
}