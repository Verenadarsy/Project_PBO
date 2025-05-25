package pbo.autocare.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TECHNICIAN")
public class Technician extends User {

    public Technician() {
        // Constructor default diperlukan oleh JPA
    }

    public Technician(String username, String password) {
        super(username, password);
    }
}