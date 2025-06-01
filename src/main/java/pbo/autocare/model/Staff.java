package pbo.autocare.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("STAFF") // Harus sesuai dengan nilai di kolom user_type di database
public class Staff extends User {

    // Constructor default diperlukan oleh JPA
    public Staff() {
        super(); // Memanggil konstruktor default User
    }

    // Konstruktor dengan username dan password (jika hanya itu yang diisi)
    public Staff(String username, String password) {
        super(username, password); // Memanggil konstruktor User(String, String)
        // Atribut lain (email, fullName, phoneNumber) akan null jika tidak diisi
    }

    // Konstruktor yang lebih lengkap untuk Staff
    public Staff(String username, String password, String email, String fullName, String phoneNumber) {
        super(username, password, email, fullName, phoneNumber); // Memanggil konstruktor User(String,String,String,String,String)
    }

    // Konstruktor paling lengkap, termasuk ID (untuk loading dari DB)
    public Staff(Long id, String username, String password, String email, String fullName, String phoneNumber) {
        super(id, username, password, email, fullName, phoneNumber);
    }

    // Staff tidak memiliki atribut spesifik di sini,
    // jadi tidak ada getter/setter tambahan untuk Staff.
    // Jika nanti ada atribut khusus Staff, bisa ditambahkan di sini.
}