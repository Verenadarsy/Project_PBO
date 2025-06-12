package pbo.autocare.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn; // Import untuk relasi ManyToOne
import jakarta.persistence.ManyToOne; // Import untuk anotasi JoinColumn

@Entity
@DiscriminatorValue("TECHNICIAN") // Nilai yang akan disimpan di kolom 'user_type' saat objek Technician disimpan
public class Technician extends User {

    // Deklarasi properti spesifik Technician: Spesialisasi
    // @ManyToOne menunjukkan relasi many Technicians to one Specialization
    // @JoinColumn mendefinisikan kolom foreign key di tabel 'users' (atau tabel Technician jika JOINED strategy)
     @ManyToOne(fetch = FetchType.EAGER)  // Asumsi: Banyak Teknisi bisa memiliki satu Spesialisasi
    @JoinColumn(name = "specialization_id") // Nama kolom di database yang menyimpan ID spesialisasi
    private Specialization specialization; // Objek Specialization yang terkait dengan teknisi ini

    // Constructor default diperlukan oleh JPA untuk membuat instance objek dari database.
    public Technician() {
        super(); // Memanggil konstruktor default dari kelas User (induk)
    }

    // Constructor dasar untuk membuat objek Technician hanya dengan username dan password.
    // Properti lain (email, fullName, phoneNumber) akan diisi melalui setters atau Auditing.
    public Technician(String username, String password) {
        super(username, password); // Memanggil konstruktor User dengan username dan password
    }

    /**
     * Constructor lengkap untuk membuat objek Technician dengan semua properti dasar User.
     * Ini digunakan oleh UserServiceImpl.createSuperUser() untuk mengisi semua kolom NOT NULL.
     * @param username Username dari Technician.
     * @param password Password Technician (harus sudah di-encode sebelum dipanggil).
     * @param email Email Technician.
     * @param fullName Nama lengkap Technician.
     * @param phoneNumber Nomor telepon Technician.
     */
    public Technician(String username, String password, String email, String fullName, String phoneNumber) {
        super(username, password, email, fullName, phoneNumber); // Memanggil konstruktor 5 parameter di kelas User
    }

    /**
     * Constructor paling lengkap, termasuk properti spesifik Technician (Specialization).
     * Gunakan ini jika Anda ingin menginisialisasi Spesialisasi saat membuat objek Technician.
     * @param username Username dari Technician.
     * @param password Password Technician.
     * @param email Email Technician.
     * @param fullName Nama lengkap Technician.
     * @param phoneNumber Nomor telepon Technician.
     * @param specialization Objek Specialization yang terkait dengan Technician ini.
     */
    public Technician(String username, String password, String email, String fullName, String phoneNumber, Specialization specialization) {
        super(username, password, email, fullName, phoneNumber);
        this.specialization = specialization; // Inisialisasi properti spesifik Technician
    }

    // --- Getters dan Setters untuk properti spesifik Technician (specialization) ---
    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }
}