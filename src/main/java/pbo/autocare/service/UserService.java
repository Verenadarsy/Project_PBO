package pbo.autocare.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;

import pbo.autocare.dto.CustomerFormDTO;
import pbo.autocare.model.Customer;      // Import Customer
import pbo.autocare.model.User;         // Import User

// Antarmuka ini mengumpulkan semua operasi terkait User dan turunannya.
// Mengextend UserDetailsService agar UserServiceImpl tetap bisa berfungsi untuk Spring Security
// dan semua metode Security terkait UserDetails terdefinisi di sini.
public interface UserService extends UserDetailsService {

    // Metode dari UserDetailsService (diimplementasikan oleh UserServiceImpl):
    // UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    // (Tidak perlu mendeklarasikan ulang di sini karena sudah diwarisi dari UserDetailsService)

    // Metode untuk pendaftaran Customer baru (digunakan oleh form registrasi, misalnya)
    Customer registerNewCustomer(Customer customer);

    // Metode untuk membuat user awal (Admin, Technician, Staff, Customer) saat startup
    void createSuperUser(String username, String rawPassword, String userType);

    // Metode pencarian user (objek User) berdasarkan username (gunakan ini atau findUserEntityByUsername)
    Optional<User> findUserByUsername(String username); // <-- Metode yang diminta

    // Metode pencarian user (objek User) berdasarkan username (alternatif jika Anda punya keduanya)
    Optional<User> findUserEntityByUsername(String username);


    // Metode pemeriksaan keberadaan username
    boolean existsByUsername(String username);

    // Metode untuk mendapatkan semua user bertipe Staff
    List<User> getAllStaff();

    // Metode untuk mendapatkan semua user bertipe Teknisi
    List<User> getAllTechnicians();

    // --- Metode CRUD untuk entitas Customer ---
    // Metode untuk mendapatkan semua user bertipe Pelanggan (Customer)
    List<Customer> getAllCustomers();

    // Metode untuk menyimpan/menambah Pelanggan baru dari DTO
    Customer saveNewCustomer(CustomerFormDTO customerDTO);

    // Metode untuk mendapatkan satu Pelanggan berdasarkan ID
    Optional<Customer> getCustomerById(Long id);

    // Metode untuk memperbarui data Pelanggan dari DTO
    Customer updateCustomer(Long id, CustomerFormDTO customerDTO);

    // Metode untuk menghapus Pelanggan berdasarkan ID
    void deleteCustomer(Long id);
}