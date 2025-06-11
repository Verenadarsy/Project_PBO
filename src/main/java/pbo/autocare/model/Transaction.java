// src/main/java/pbo/autocare/model/Transaction.java
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

    // --- MODIFIKASI INI ---
    @Enumerated(EnumType.STRING) // Simpan ENUM sebagai String di DB
    @Column(name = "payment_method", nullable = false, length = 50)
    private PaymentMethod paymentMethod; // Ubah tipe data
    // --- AKHIR MODIFIKASI ---

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false)
    private TransactionStatus transactionStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    // Enum untuk TransactionStatus
    public enum TransactionStatus {
        PAID, PENDING, FAILED, REFUNDED
    }

    // --- ENUM BARU UNTUK PAYMENT METHOD ---
    public enum PaymentMethod {
        UNSPECIFIED, CASH, DEBIT, CREDIT_CARD, E_WALLET
    }
    // --- AKHIR ENUM BARU ---

    // Default constructor
    public Transaction() {
        this.transactionDate = new Timestamp(System.currentTimeMillis());
        this.transactionStatus = TransactionStatus.PAID;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    // Constructor untuk membuat Transaksi baru
    // --- MODIFIKASI INI ---
    public Transaction(ServiceOrder serviceOrder, BigDecimal amount, PaymentMethod paymentMethod) { // Ubah tipe data paymentMethod
        this(null, serviceOrder, amount, new Timestamp(System.currentTimeMillis()), paymentMethod, TransactionStatus.PAID,
             new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
    }
    // --- AKHIR MODIFIKASI ---

    // Constructor lengkap (untuk memuat dari DB)
    // --- MODIFIKASI INI ---
    public Transaction(Long id, ServiceOrder serviceOrder, BigDecimal amount, Timestamp transactionDate,
                       PaymentMethod paymentMethod, TransactionStatus transactionStatus, // Ubah tipe data paymentMethod
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
    // --- AKHIR MODIFIKASI ---

    // Getters dan Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ServiceOrder getServiceOrder() { return serviceOrder; }
    public void setServiceOrder(ServiceOrder serviceOrder) { this.serviceOrder = serviceOrder; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Timestamp getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Timestamp transactionDate) { this.transactionDate = transactionDate; }
    // --- MODIFIKASI INI ---
    public PaymentMethod getPaymentMethod() { return paymentMethod; } // Ubah return type
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; } // Ubah parameter type
    // --- AKHIR MODIFIKASI ---
    public TransactionStatus getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(TransactionStatus transactionStatus) { this.transactionStatus = transactionStatus; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}