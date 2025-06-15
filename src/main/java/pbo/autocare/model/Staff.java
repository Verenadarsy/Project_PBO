package pbo.autocare.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("STAFF") 
public class Staff extends User {


    public Staff() {
        super(); 
    }

    public Staff(String username, String password) {
        super(username, password); 
    }

    /**
     * @param username Username dari Staff.
     * @param password Password Staff (harus sudah di-encode sebelum dipanggil).
     * @param email Email Staff.
     * @param fullName Nama lengkap Staff.
     * @param phoneNumber Nomor telepon Staff.
     */
    public Staff(String username, String password, String email, String fullName, String phoneNumber) {
        super(username, password, email, fullName, phoneNumber); 
    }
}

