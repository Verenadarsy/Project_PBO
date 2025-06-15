package pbo.autocare.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn; 
import jakarta.persistence.ManyToOne; 

@Entity
@DiscriminatorValue("TECHNICIAN") 
public class Technician extends User {

    @ManyToOne(fetch = FetchType.EAGER)  
    @JoinColumn(name = "specialization_id") 
    private Specialization specialization; 

    public Technician() {
        super(); 
    }

    public Technician(String username, String password) {
        super(username, password);
    }

    /**
     * @param username Username dari Technician.
     * @param password Password Technician (harus sudah di-encode sebelum dipanggil).
     * @param email Email Technician.
     * @param fullName Nama lengkap Technician.
     * @param phoneNumber Nomor telepon Technician.
     */
    public Technician(String username, String password, String email, String fullName, String phoneNumber) {
        super(username, password, email, fullName, phoneNumber); 
    }

    /**
     * @param username Username dari Technician.
     * @param password Password Technician.
     * @param email Email Technician.
     * @param fullName Nama lengkap Technician.
     * @param phoneNumber Nomor telepon Technician.
     * @param specialization Objek Specialization yang terkait dengan Technician ini.
     */
    public Technician(String username, String password, String email, String fullName, String phoneNumber, Specialization specialization) {
        super(username, password, email, fullName, phoneNumber);
        this.specialization = specialization; 
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }
}
