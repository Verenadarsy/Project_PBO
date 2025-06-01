// src/main/java/pbo/autocare/model/Specialization.java
package pbo.autocare.model;

import jakarta.persistence.*; // Pastikan ini jakarta.persistence

@Entity
@Table(name = "specializations") // Nama tabel baru
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Gunakan Long agar konsisten dengan User ID

    @Column(unique = true, nullable = false, length = 10) // Contoh length untuk kode
    private String code; // Ex: SB, PM, AC, TU, BR

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description; // Ex: Service Berkala, Perbaikan Mesin

    // Default constructor diperlukan oleh JPA
    public Specialization() {}

    public Specialization(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public Specialization(Long id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    // Getters dan Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Specialization{" +
               "id=" + id +
               ", code='" + code + '\'' +
               ", description='" + description + '\'' +
               '}';
    }
}