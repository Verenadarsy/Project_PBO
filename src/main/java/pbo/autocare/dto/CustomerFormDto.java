package pbo.autocare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
// import lombok.Getter; // Jika pakai Lombok
// import lombok.Setter; // Jika pakai Lombok
// import lombok.NoArgsConstructor; // Jika pakai Lombok
// import lombok.AllArgsConstructor; // Jika pakai Lombok

// @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CustomerFormDto {

    private Long id; // Untuk edit

    @NotBlank(message = "Username tidak boleh kosong")
    @Size(min = 3, max = 50, message = "Username harus antara 3 dan 50 karakter")
    private String username;

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 6, message = "Password minimal 6 karakter") // Aturan minimal password
    private String password;

    @NotBlank(message = "Nama lengkap tidak boleh kosong")
    @Size(min = 3, max = 100, message = "Nama lengkap harus antara 3 dan 100 karakter")
    private String fullName;

    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotBlank(message = "Nomor telepon tidak boleh kosong")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Nomor telepon harus berupa angka antara 10 dan 15 digit")
    private String phoneNumber;

    // Constructor default (jika tidak pakai Lombok)
    public CustomerFormDto() {}

    // Constructor untuk transfer data dari Entitas ke DTO (untuk edit form)
    public CustomerFormDto(Long id, String username, String password, String fullName, String email, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getters dan Setters (jika tidak pakai Lombok)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}