package pbo.autocare.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import pbo.autocare.dto.CustomerFormDTO; // Pastikan Anda memiliki DTO ini
import pbo.autocare.model.Admin;
import pbo.autocare.model.Customer;
import pbo.autocare.model.Staff;
import pbo.autocare.model.Technician;
import pbo.autocare.model.User;
import pbo.autocare.repository.UserRepository; // Pastikan Anda memiliki UserRepository ini

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection: Spring akan secara otomatis menyuntikkan UserRepository dan PasswordEncoder
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Metode ini digunakan oleh Spring Security untuk memuat detail pengguna
     * berdasarkan username untuk proses otentikasi.
     * @param username Username dari pengguna yang akan dimuat.
     * @return Objek UserDetails yang berisi detail pengguna.
     * @throws UsernameNotFoundException Jika pengguna dengan username yang diberikan tidak ditemukan.
     */
    @Override
    @Transactional(readOnly = true) // Operasi baca saja, tidak memodifikasi data.
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Mencari pengguna di database berdasarkan username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Membuat daftar hak akses/otoritas (roles) untuk pengguna.
        // Asumsi: setiap user memiliki satu peran yang sesuai dengan user_type mereka.
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getUserType().toUpperCase()) // Contoh: ROLE_ADMIN, ROLE_CUSTOMER
        );

        // Mengembalikan objek UserDetails yang dibutuhkan oleh Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // Password harus sudah di-encode di database
                authorities
        );
    }

    /**
     * Mendaftarkan pengguna baru dengan peran Customer.
     * Password akan di-encode sebelum disimpan.
     * @param customer Objek Customer yang akan didaftarkan.
     * @return Objek Customer yang sudah tersimpan di database.
     */
    @Override
    @Transactional // Operasi tulis, memerlukan transaksi.
    public Customer registerNewCustomer(Customer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword())); // Encode password untuk keamanan
        // created_at dan updated_at akan diisi otomatis oleh JPA Auditing
        return userRepository.save(customer);
    }

    /**
     * Membuat user awal (super user) seperti Admin, Teknisi, Staff, atau Customer
     * saat aplikasi pertama kali dijalankan (jika user belum ada).
     * @param username Username dari user yang akan dibuat.
     * @param rawPassword Password user (belum di-encode).
     * @param userType Tipe user (contoh: "ADMIN", "TECHNICIAN", "STAFF", "CUSTOMER").
     */
    @Override
    @Transactional // Operasi tulis, memerlukan transaksi.
    public void createSuperUser(String username, String rawPassword, String userType) {
        // Cek jika user dengan username tersebut sudah ada di database
        if (userRepository.existsByUsername(username)) {
            System.out.println("Super user '" + username + "' already exists. Skipping creation.");
            return;
        }

        User user;
        String encodedPassword = passwordEncoder.encode(rawPassword); // Encode password untuk keamanan

        // Data default untuk properti yang wajib diisi (NOT NULL) pada entitas User
        // Ini untuk mencegah error Column '...' cannot be null
        String defaultEmail = username + "@autocare.com";
        String defaultFullName = "Nama " + userType.substring(0, 1).toUpperCase() + userType.substring(1).toLowerCase();
        String defaultPhoneNumber = "08" + String.format("%010d", System.currentTimeMillis() % 10000000000L); // Nomor acak unik

        // Membuat instance sub-kelas User (Admin, Technician, Staff, Customer)
        // menggunakan konstruktor yang mengisi semua properti wajib.
        switch (userType.toUpperCase()) {
            case "ADMIN" -> user = new Admin(username, encodedPassword, defaultEmail, defaultFullName, defaultPhoneNumber);
            case "TECHNICIAN" -> user = new Technician(username, encodedPassword, defaultEmail, defaultFullName, defaultPhoneNumber);
            case "STAFF" -> user = new Staff(username, encodedPassword, defaultEmail, defaultFullName, defaultPhoneNumber);
            case "CUSTOMER" -> user = new Customer(username, encodedPassword, defaultEmail, defaultFullName, defaultPhoneNumber);
            default -> throw new IllegalArgumentException("Invalid user type: " + userType);
        }

        userRepository.save(user); // Menyimpan user baru ke database
        System.out.println("Super user '" + username + "' with type '" + userType + "' created successfully.");
    }

    /**
     * Mencari objek User entitas berdasarkan username.
     * @param username Username yang dicari.
     * @return Optional yang berisi objek User jika ditemukan, kosong jika tidak.
     */
    @Override
    @Transactional(readOnly = true) // Operasi baca saja.
    public Optional<User> findUserEntityByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Metode untuk mencari objek User entitas berdasarkan username.
     * Ini adalah metode yang spesifik Anda ingin ada di interface UserService.
     * Dalam implementasi, ia memanggil findUserEntityByUsername().
     * @param username Username yang dicari.
     * @return Optional yang berisi objek User jika ditemukan, kosong jika tidak.
     */
    @Override
    @Transactional(readOnly = true) // Operasi baca saja.
    public Optional<User> findUserByUsername(String username) {
        return findUserEntityByUsername(username); // Memanggil metode yang sudah ada
    }

    /**
     * Memeriksa apakah username sudah ada di database.
     * @param username Username yang akan diperiksa.
     * @return true jika username sudah ada, false jika belum.
     */
    @Override
    @Transactional(readOnly = true) // Operasi baca saja.
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Mengambil daftar semua user dengan peran Staff.
     * @return List objek User yang bertipe Staff.
     */
    @Override
    @Transactional(readOnly = true) // Operasi baca saja.
    public List<User> getAllStaff() {
        return userRepository.findByUserType("STAFF");
    }

    /**
     * Mengambil daftar semua user dengan peran Technician.
     * @return List objek User yang bertipe Technician.
     */
    @Override
    @Transactional(readOnly = true) // Operasi baca saja.
    public List<User> getAllTechnicians() {
        return userRepository.findByUserType("TECHNICIAN");
    }

    /**
     * Mengambil daftar semua user dengan peran Customer.
     * @return List objek Customer.
     */
    @Override
    @Transactional(readOnly = true) // Operasi baca saja.
    public List<Customer> getAllCustomers() {
        return userRepository.findByUserType("CUSTOMER")
                             .stream()
                             .filter(user -> user instanceof Customer) // Filter untuk memastikan ini adalah instance Customer
                             .map(user -> (Customer) user) // Cast objek User ke Customer
                             .collect(Collectors.toList()); // Mengumpulkan hasil ke dalam List<Customer>
    }

    /**
     * Menyimpan Customer baru dari Data Transfer Object (DTO).
     * Melakukan validasi duplikasi username dan email.
     * @param CustomerDTO DTO yang berisi data Customer baru.
     * @return Objek Customer yang sudah disimpan.
     * @throws ResponseStatusException Jika username atau email sudah digunakan.
     */
    @Override
    @Transactional // Operasi tulis, memerlukan transaksi.
    public Customer saveNewCustomer(CustomerFormDTO CustomerDTO) {
        // Cek duplikasi username untuk customer baru
        if (userRepository.existsByUsername(CustomerDTO.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username '" + CustomerDTO.getUsername() + "' sudah digunakan. Pilih username lain.");
        }
        // Cek duplikasi email untuk customer baru
        // Asumsi: findByEmail mengembalikan Optional<User>
        if (userRepository.findByEmail(CustomerDTO.getEmail()).isPresent()) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email '" + CustomerDTO.getEmail() + "' sudah terdaftar. Gunakan email lain.");
        }

        // Membuat objek Customer dari DTO dan meng-encode password
        Customer customer = new Customer(
            CustomerDTO.getUsername(),
            passwordEncoder.encode(CustomerDTO.getPassword()), // Encode password dari DTO
            CustomerDTO.getEmail(),
            CustomerDTO.getFullName(),
            CustomerDTO.getPhoneNumber()
        );
        // created_at dan updated_at akan otomatis terisi oleh JPA Auditing

        return userRepository.save(customer); // Menyimpan Customer
    }

    /**
     * Mengambil satu objek Customer berdasarkan ID.
     * @param id ID Customer yang dicari.
     * @return Optional yang berisi objek Customer jika ditemukan dan merupakan Customer.
     */
    @Override
    @Transactional(readOnly = true) // Operasi baca saja.
    public Optional<Customer> getCustomerById(Long id) {
        // Mencari user berdasarkan ID, kemudian memfilter dan meng-cast ke Customer
        return userRepository.findById(id)
                             .filter(user -> user instanceof Customer)
                             .map(user -> (Customer) user);
    }

    /**
     * Memperbarui data Customer yang sudah ada dari DTO.
     * Melakukan validasi duplikasi username dan email jika diubah.
     * @param id ID Customer yang akan diperbarui.
     * @param CustomerDTO DTO yang berisi data pembaruan.
     * @return Objek Customer yang sudah diperbarui.
     * @throws ResponseStatusException Jika Customer tidak ditemukan, bukan Customer, atau ada duplikasi data.
     */
    @Override
    @Transactional // Operasi tulis, memerlukan transaksi.
    public Customer updateCustomer(Long id, CustomerFormDTO CustomerDTO) {
        // Mencari user berdasarkan ID
        return userRepository.findById(id)
            .map(existingUser -> {
                // Memastikan user yang ditemukan adalah instance dari Customer
                if (!(existingUser instanceof Customer)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with ID " + id + " is not a Customer.");
                }
                Customer existingCustomer = (Customer) existingUser;

                // Cek duplikasi username jika username diubah
                if (!existingCustomer.getUsername().equals(CustomerDTO.getUsername())) {
                    if (userRepository.existsByUsername(CustomerDTO.getUsername())) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username '" + CustomerDTO.getUsername() + "' sudah digunakan.");
                    }
                }
                // Cek duplikasi email jika email diubah
                if (!existingCustomer.getEmail().equals(CustomerDTO.getEmail())) {
                    Optional<User> userWithSameEmail = userRepository.findByEmail(CustomerDTO.getEmail());
                    if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(id)) { // Pastikan email bukan milik user yang sama
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email '" + CustomerDTO.getEmail() + "' sudah terdaftar.");
                    }
                }

                // Perbarui properti Customer
                existingCustomer.setUsername(CustomerDTO.getUsername());
                // Password hanya diperbarui jika ada password baru di form DTO
                if (CustomerDTO.getPassword() != null && !CustomerDTO.getPassword().isEmpty()) {
                    existingCustomer.setPassword(passwordEncoder.encode(CustomerDTO.getPassword()));
                }
                existingCustomer.setFullName(CustomerDTO.getFullName());
                existingCustomer.setEmail(CustomerDTO.getEmail());
                existingCustomer.setPhoneNumber(CustomerDTO.getPhoneNumber());

                return userRepository.save(existingCustomer); // Simpan perubahan Customer
            })
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer with ID " + id + " not found."));
    }

    /**
     * Menghapus Customer berdasarkan ID.
     * @param id ID Customer yang akan dihapus.
     * @throws ResponseStatusException Jika Customer tidak ditemukan atau bukan Customer.
     */
    @Override
    @Transactional // Operasi tulis, memerlukan transaksi.
    public void deleteCustomer(Long id) {
        // Mencari user berdasarkan ID, lalu memastikan dan meng-cast ke Customer
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty() || !(userOptional.get() instanceof Customer)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer with ID " + id + " not found or is not a Customer.");
        }
        userRepository.deleteById(id); // Menghapus Customer
    }
}