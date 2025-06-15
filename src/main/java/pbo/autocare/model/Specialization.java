package pbo.autocare.model;

import jakarta.persistence.*;

@Entity
@Table(name = "specializations")
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 10)
    private String code; 

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description; 

    @Transient 
    private Long technicianCount; 

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

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