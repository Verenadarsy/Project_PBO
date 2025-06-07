package pbo.autocare.model;

import java.sql.Timestamp; // Pastikan ini jakarta.persistence jika SB 3+

import org.springframework.data.annotation.CreatedDate;     // <-- TAMBAHKAN INI
import org.springframework.data.annotation.LastModifiedDate;  // <-- TAMBAHKAN INI
import org.springframework.data.jpa.domain.support.AuditingEntityListener;   // <-- TAMBAHKAN INI

import jakarta.persistence.Column;      // <-- TAMBAHKAN INI
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users") // Nama tabel di database sesuai screenshot kamu
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING) // Kolom untuk membedakan tipe user
@EntityListeners(AuditingEntityListener.class) // Aktifkan Auditing untuk @CreatedDate/@LastModifiedDate
public abstract class User { // Kelas abstrak untuk konsep umum

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username tidak boleh kosong") // <-- TAMBAHKAN VALIDASI INI
    @Size(min = 3, max = 50, message = "Username harus antara 3 dan 50 karakter") // <-- TAMBAHKAN VALIDASI INI
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // Password akan disimpan ter-enkripsi

    // --- PROPERTI YANG PERLU VALIDASI DAN NULLABLE=FALSE ---
    @NotBlank(message = "Email tidak boleh kosong") // <-- TAMBAHKAN VALIDASI INI
    @Email(message = "Format email tidak valid") // <-- TAMBAHKAN VALIDASI INI
    @Column(name = "email", nullable = false, unique = true) // <-- TAMBAHKAN nullable=false DAN unique=true
    private String email;

    @NotBlank(message = "Nama lengkap tidak boleh kosong") // <-- TAMBAHKAN VALIDASI INI
    @Size(min = 3, max = 100, message = "Nama lengkap harus antara 3 dan 100 karakter") // <-- TAMBAHKAN VALIDASI INI
    @Column(name = "full_name", nullable = false) // <-- TAMBAHKAN nullable=false
    private String fullName;

    @NotBlank(message = "Nomor telepon tidak boleh kosong") // <-- TAMBAHKAN VALIDASI INI
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Nomor telepon harus berupa angka antara 10 dan 15 digit") // <-- TAMBAHKAN VALIDASI INI
    @Column(name = "phone_number", nullable = false) // <-- TAMBAHKAN nullable=false
    private String phoneNumber;
    // --- AKHIR PROPERTI YANG PERLU VALIDASI DAN NULLABLE=FALSE ---

    // created_at dan updated_at sudah benar dengan anotasi auditing dan nullable=false
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    @Column(name = "user_type", insertable = false, updatable = false) // 'insertable=false' dan 'updatable=false' agar Hibernate tidak mencoba mengelola kolom ini secara manual, karena sudah dikelola oleh @DiscriminatorColumn.
    private String userType;

    public User(String userType) {
        this.userType = userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    // Constructor default diperlukan oleh JPA
    public User() {}

    // Konstruktor dasar untuk semua tipe user
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        // created_at dan updated_at akan diisi otomatis oleh Spring Data JPA Auditing
    }

    // Konstruktor yang lebih lengkap untuk kemudahan subclass/saat membuat user baru
    // Subclass seperti Customer akan memanggil super(username, password, email, fullName, phoneNumber)
    public User(String username, String password, String email, String fullName, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    // Constructor paling lengkap, termasuk ID (untuk loading dari DB atau untuk testing/data seeding)
    public User(Long id, String username, String password, String email, String fullName, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }


    // Getters dan Setters (Enkapsulasi)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    // Metode untuk mendapatkan userType (role) secara dinamis
    // Ini bukan field yang dipersistenkan, tapi metode untuk mendapatkan nilai discriminator
    // Pastikan kelas Admin, Technician, Staff, Customer ada di package yang sama atau diimpor
    public String getUserType() {
        if (this instanceof Admin) return "ADMIN";
        if (this instanceof Technician) return "TECHNICIAN";
        if (this instanceof Staff) return "STAFF";
        if (this instanceof Customer) return "CUSTOMER";
        return "UNKNOWN"; // Fallback jika tidak ada yang cocok
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", userType='" + getUserType() + '\'' + // Menggunakan method dinamis
               '}';
    }
}