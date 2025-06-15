package pbo.autocare.repository;

import pbo.autocare.model.Transaction;
import pbo.autocare.model.Transaction.TransactionStatus; 
import pbo.autocare.model.ServiceOrder; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByServiceOrder(ServiceOrder serviceOrder);
    Optional<Transaction> findByServiceOrderId(Long serviceOrderId); 
    List<Transaction> findByTransactionStatus(TransactionStatus transactionStatus);
    List<Transaction> findByPaymentMethod(String paymentMethod);
}