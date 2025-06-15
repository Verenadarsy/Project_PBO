package pbo.autocare.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", unique = true, nullable = false)
    private ServiceOrder serviceOrder;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_date", nullable = false)
    private Timestamp transactionDate;

    @Enumerated(EnumType.STRING) 
    @Column(name = "payment_method", nullable = false, length = 50)
    private PaymentMethod paymentMethod; 

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false)
    private TransactionStatus transactionStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    public enum TransactionStatus {
        PAID, PENDING, FAILED, REFUNDED
    }

    public enum PaymentMethod {
        UNSPECIFIED, CASH, DEBIT, CREDIT_CARD, E_WALLET
    }

    public Transaction() {
        this.transactionDate = new Timestamp(System.currentTimeMillis());
        this.transactionStatus = TransactionStatus.PAID;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public Transaction(ServiceOrder serviceOrder, BigDecimal amount, PaymentMethod paymentMethod) { 
        this(null, serviceOrder, amount, new Timestamp(System.currentTimeMillis()), paymentMethod, TransactionStatus.PAID,
             new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
    }
  
    public Transaction(Long id, ServiceOrder serviceOrder, BigDecimal amount, Timestamp transactionDate,
                       PaymentMethod paymentMethod, TransactionStatus transactionStatus,
                       Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.serviceOrder = serviceOrder;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.paymentMethod = paymentMethod;
        this.transactionStatus = transactionStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ServiceOrder getServiceOrder() { return serviceOrder; }
    public void setServiceOrder(ServiceOrder serviceOrder) { this.serviceOrder = serviceOrder; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Timestamp getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Timestamp transactionDate) { this.transactionDate = transactionDate; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; } 
    public TransactionStatus getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(TransactionStatus transactionStatus) { this.transactionStatus = transactionStatus; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}