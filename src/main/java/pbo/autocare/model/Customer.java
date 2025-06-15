package pbo.autocare.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {

    public Customer() {
        super(); 
    }

    public Customer(String username, String password, String fullName, String email, String phoneNumber) {
        super(username, password); 
        super.setFullName(fullName);
        super.setEmail(email);
        super.setPhoneNumber(phoneNumber);
    }
}

