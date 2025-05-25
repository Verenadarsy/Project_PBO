package pbo.autocare.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // Nama tabel di database
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Strategi pewarisan: semua kelas turunan dalam satu tabel
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING) // Kolom untuk membedakan tipe user
public abstract class User { // Abstract class, karena User itu konsep umum, bukan entitas konkret

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // Password akan disimpan ter-enkripsi

    @Column(name = "user_type", insertable = false, updatable = false) // Kolom discriminator, tidak perlu diisi manual
    private String userType;

    // Constructor, Getters, Setters
    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    // userType tidak punya setter publik karena diatur oleh @DiscriminatorValue pada subclass
}