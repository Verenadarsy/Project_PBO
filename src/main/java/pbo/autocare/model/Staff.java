package pbo.autocare.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("STAFF") // Nilai yang akan disimpan di kolom 'user_type' saat objek Staff disimpan
public class Staff extends User {

    // Constructor default diperlukan oleh JPA untuk membuat instance objek dari database.
    public Staff() {
        super(); // Memanggil konstruktor default dari kelas User (induk)
    }

    // Constructor dasar untuk membuat objek Staff hanya dengan username dan password.
    // Properti lain (email, fullName, phoneNumber) akan diisi melalui setters atau Auditing (createdAt, updatedAt).
    public Staff(String username, String password) {
        super(username, password); // Memanggil konstruktor User dengan username dan password
    }

    /**
     * Constructor lengkap untuk membuat objek Staff dengan semua properti dasar User.
     * Ini digunakan oleh UserServiceImpl.createSuperUser() untuk mengisi semua kolom NOT NULL.
     * @param username Username dari Staff.
     * @param password Password Staff (harus sudah di-encode sebelum dipanggil).
     * @param email Email Staff.
     * @param fullName Nama lengkap Staff.
     * @param phoneNumber Nomor telepon Staff.
     */
    public Staff(String username, String password, String email, String fullName, String phoneNumber) {
        super(username, password, email, fullName, phoneNumber); // Memanggil konstruktor 5 parameter di kelas User
    }

    // Anda dapat menambahkan properti atau metode spesifik untuk Staff di sini jika diperlukan.
    // Contoh:
    // private String department;
    // public String getDepartment() { return department; }
    // public void setDepartment(String department) { this.department = department; }
}