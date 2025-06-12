package pbo.autocare.dto;

// Ini adalah DTO yang digunakan untuk tampilan daftar
public class SpecializationListDTO {
    private Long id;
    private String code;
    private String description;
    private Long technicianCount; // Menggunakan Long untuk total count

    // Constructor harus cocok dengan SELECT NEW di query JPA
    public SpecializationListDTO(Long id, String code, String description, Long technicianCount) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.technicianCount = technicianCount;
    }

    // Getters
    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public Long getTechnicianCount() { return technicianCount; }

    // Setters tidak diperlukan karena DTO ini hanya untuk output
}
