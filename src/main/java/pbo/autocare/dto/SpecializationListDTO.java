package pbo.autocare.dto;

public class SpecializationListDTO {
    private Long id;
    private String code;
    private String description;
    private Long technicianCount; 

    public SpecializationListDTO(Long id, String code, String description, Long technicianCount) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.technicianCount = technicianCount;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public Long getTechnicianCount() { return technicianCount; }

}
