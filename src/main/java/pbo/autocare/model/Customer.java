package pbo.autocare.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {
    // Tidak perlu deklarasi fullName, email, phoneNumber lagi
    // Tidak perlu getFullName(), setFullName() dll. lagi di sini

    public Customer() {
        super(); // Memanggil konstruktor default User
    }

    public Customer(String username, String password, String fullName, String email, String phoneNumber) {
        super(username, password); // Panggil konstruktor User untuk username dan password
        // Gunakan setter dari kelas induk untuk properti yang diwarisi
        super.setFullName(fullName);
        super.setEmail(email);
        super.setPhoneNumber(phoneNumber);
    }
}
