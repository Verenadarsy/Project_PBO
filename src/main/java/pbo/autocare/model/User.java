package pbo.autocare.model;

import jakarta.persistence.*; // Pastikan ini jakarta.persistence jika SB 3+
import java.sql.Timestamp;

@Entity
@Table(name = "users") // Nama tabel di database sesuai screenshot kamu
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING) // Kolom untuk membedakan tipe user
public abstract class User { // Kelas abstrak untuk konsep umum

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // Password akan disimpan ter-enkripsi

    @Column(name = "email") // Tambahkan field dan anotasi untuk email
    private String email;

    @Column(name = "full_name") // Tambahkan field dan anotasi untuk full_name
    private String fullName;

    @Column(name = "phone_number") // Tambahkan field dan anotasi untuk phone_number
    private String phoneNumber;

    // Hibernate akan otomatis mengisi kolom 'user_type' berdasarkan @DiscriminatorValue
    // di subclass. Jadi, kita tidak perlu mendeklarasikan field 'userType' di sini.
    // public String userType; <-- HAPUS FIELD INI

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;


    // Constructor default diperlukan oleh JPA
    public User() {}

    // Konstruktor dasar untuk semua tipe user
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        // created_at dan updated_at akan diisi otomatis oleh DB atau Hibernate
    }

    // Konstruktor yang lebih lengkap untuk kemudahan subclass
    // Subclass akan memanggil super(username, password, email, fullName, phoneNumber)
    public User(String username, String password, String email, String fullName, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    // Constructor paling lengkap, termasuk ID (untuk loading dari DB)
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

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Metode untuk mendapatkan userType (role) secara dinamis
    // Ini bukan field yang dipersistenkan, tapi metode untuk mendapatkan nilai discriminator
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