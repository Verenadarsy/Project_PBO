package pbo.autocare.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    public Admin() {
        super();
    }

    public Admin(String username, String password) {
        super(username, password); 
    }

    /**
     * @param username Username dari Admin.
     * @param password Password Admin (harus sudah di-encode sebelum dipanggil).
     * @param email Email Admin.
     * @param fullName Nama lengkap Admin.
     * @param phoneNumber Nomor telepon Admin.
     */
    public Admin(String username, String password, String email, String fullName, String phoneNumber) {
        super(username, password, email, fullName, phoneNumber); 
    }
}
