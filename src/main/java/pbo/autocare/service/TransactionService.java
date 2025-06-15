package pbo.autocare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pbo.autocare.model.ServiceOrder;
import pbo.autocare.model.Transaction;
import pbo.autocare.repository.TransactionRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public Optional<Transaction> getTransactionByServiceOrder(ServiceOrder serviceOrder) {
        return transactionRepository.findByServiceOrder(serviceOrder);
    }

    public Transaction updateTransactionStatusAndPaymentMethod(Long id,
                                                               Transaction.TransactionStatus newStatus,
                                                               Transaction.PaymentMethod newPaymentMethod) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isPresent()) {
            Transaction transaction = optionalTransaction.get();
            transaction.setTransactionStatus(newStatus);
            transaction.setPaymentMethod(newPaymentMethod);
            transaction.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            return transactionRepository.save(transaction);
        }
        return null;
    }
}