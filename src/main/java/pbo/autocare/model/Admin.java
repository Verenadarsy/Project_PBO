package pbo.autocare.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN") // Nilai yang akan disimpan di kolom 'user_type'
public class Admin extends User {

    public Admin() {
        // Constructor default diperlukan oleh JPA
    }

    public Admin(String username, String password) {
        super(username, password);
    }
}