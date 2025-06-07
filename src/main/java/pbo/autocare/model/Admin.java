package pbo.autocare.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN") // Nilai yang akan disimpan di kolom 'user_type' saat objek Admin disimpan
public class Admin extends User {

    // Constructor default diperlukan oleh JPA untuk membuat instance objek dari database.
    public Admin() {
        super(); // Memanggil konstruktor default dari kelas User (induk)
    }

    // Constructor dasar untuk membuat objek Admin hanya dengan username dan password.
    // Properti lain (email, fullName, phoneNumber) akan diisi melalui setters atau Auditing (createdAt, updatedAt).
    public Admin(String username, String password) {
        super(username, password); // Memanggil konstruktor User dengan username dan password
    }

    /**
     * Constructor lengkap untuk membuat objek Admin dengan semua properti dasar User.
     * Ini digunakan oleh UserServiceImpl.createSuperUser() untuk mengisi semua kolom NOT NULL.
     * @param username Username dari Admin.
     * @param password Password Admin (harus sudah di-encode sebelum dipanggil).
     * @param email Email Admin.
     * @param fullName Nama lengkap Admin.
     * @param phoneNumber Nomor telepon Admin.
     */
    public Admin(String username, String password, String email, String fullName, String phoneNumber) {
        super(username, password, email, fullName, phoneNumber); // Memanggil konstruktor 5 parameter di kelas User
    }

    // Anda dapat menambahkan properti atau metode spesifik untuk Admin di sini jika diperlukan.
    // Contoh:
    // private String adminSpecificField;
    // public String getAdminSpecificField() { return adminSpecificField; }
    // public void setAdminSpecificField(String adminSpecificField) { this.adminSpecificField = adminSpecificField; }
}