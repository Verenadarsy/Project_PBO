// src/main/java/pbo/autocare/model/Specialization.java
package pbo.autocare.model;

import jakarta.persistence.*;

@Entity
@Table(name = "specializations")
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 10)
    private String code; // Ex: SB, PM, AC, TU, BR

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description; // Ex: Service Berkala, Perbaikan Mesin

    // Kita akan mendapatkan jumlah teknisi dari query terpisah,
    // jadi tidak perlu menyimpan koleksi teknisi di sini.
    @Transient // Menandakan bahwa field ini tidak dipersistenkan ke database
    private Long technicianCount; // Gunakan Long untuk konsistensi dengan ID


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

    // Getter dan Setter untuk technicianCount (Transient)
    public Long getTechnicianCount() {
        return technicianCount;
    }

    public void setTechnicianCount(Long technicianCount) {
        this.technicianCount = technicianCount;
    }

    @Override
    public String toString() {
        return "Specialization{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}