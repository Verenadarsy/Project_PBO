package pbo.autocare.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("STAFF")
public class Staff extends User {

    public Staff() {
        // Constructor default diperlukan oleh JPA
    }

    public Staff(String username, String password) {
        super(username, password);
    }
}