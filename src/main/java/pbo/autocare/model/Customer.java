package pbo.autocare.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email; // Import ini
import jakarta.validation.constraints.NotBlank; // Import ini
import jakarta.validation.constraints.Pattern; // Import ini
import jakarta.validation.constraints.Size; // Import ini

@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {

    // Tambahkan validasi untuk username dan password di User.java juga jika diperlukan.
    // Contohnya di User.java:
    // @NotBlank(message = "Username tidak boleh kosong")
    // @Size(min = 4, max = 50, message = "Username harus antara 4 dan 50 karakter")
    // private String username;
    // @NotBlank(message = "Password tidak boleh kosong")
    // @Size(min = 6, message = "Password minimal 6 karakter")
    // private String password;

    @NotBlank(message = "Nama lengkap tidak boleh kosong")
    @Size(min = 3, max = 100, message = "Nama lengkap harus antara 3 dan 100 karakter")
    private String fullName;

    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotBlank(message = "Nomor telepon tidak boleh kosong")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Nomor telepon harus berupa angka antara 10 dan 15 digit")
    private String phoneNumber;

    public Customer() {
        // Constructor default diperlukan oleh JPA
    }

    public Customer(String username, String password, String fullName, String email, String phoneNumber) {
        super(username, password);
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters (pastikan sudah ada)
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}