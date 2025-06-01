// src/main/java/pbo/autocare/model/Technician.java
package pbo.autocare.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@DiscriminatorValue("TECHNICIAN")
public class Technician extends User {

    // HAPUS: @Column(name = "specialization") private String specialization;

    @ManyToOne // Setiap Technician punya satu Specialization
    @JoinColumn(name = "specialization_id") // Kolom FK di tabel 'users' (akan dibuat Hibernate)
    private Specialization specialization; // Ganti tipe data ke objek Specialization

    // Constructor default diperlukan oleh JPA
    public Technician() {
        super();
    }

    // Constructor dengan username dan password (jika hanya itu yang diisi)
    public Technician(String username, String password) {
        super(username, password);
    }

    // Constructor lebih lengkap, termasuk Specialization
    public Technician(String username, String password, String email, String fullName, String phoneNumber, Specialization specialization) {
        super(username, password, email, fullName, phoneNumber);
        this.specialization = specialization; // Inisialisasi objek Specialization
    }

    // Constructor paling lengkap, termasuk ID (untuk loading dari DB)
    public Technician(Long id, String username, String password, String email, String fullName, String phoneNumber, Specialization specialization) {
        super(id, username, password, email, fullName, phoneNumber);
        this.specialization = specialization;
    }

    // Getter dan Setter untuk specialization (tipe objek Specialization)
    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }
}