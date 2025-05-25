package pbo.autocare.service; // Perhatikan package name Anda, saya akan pakai pbo.autocare.service

import pbo.autocare.model.Admin;
import pbo.autocare.model.Customer;
import pbo.autocare.model.Staff;
import pbo.autocare.model.Technician;
import pbo.autocare.model.User;
import pbo.autocare.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional; // Tetap import karena userRepository.findByUsername() mengembalikan Optional

@Service
public class UserServiceImpl implements UserService, UserDetailsService { // PERBAIKI NAMA KELAS DI SINI!

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Ini adalah metode utama yang harus diimplementasikan dari UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getUserType().toUpperCase())
        );

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    // Ini adalah implementasi dari interface UserService yang Anda buat
    @Override
    public Customer registerNewCustomer(Customer customer) {
        // Enkripsi password sebelum menyimpan
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        return userRepository.save(customer);
    }

    // Metode untuk membuat superakun (admin, technician, staff) secara programatis
    public void createSuperUser(String username, String rawPassword, String userType) {
        // Cek apakah user sudah ada sebelum membuat
        if (userRepository.existsByUsername(username)) {
            System.out.println("Super user '" + username + "' already exists. Skipping creation.");
            return;
        }

        User user;
        String encodedPassword = passwordEncoder.encode(rawPassword);

        switch (userType.toUpperCase()) {
            case "ADMIN":
                user = new Admin(username, encodedPassword);
                break;
            case "TECHNICIAN":
                user = new Technician(username, encodedPassword);
                break;
            case "STAFF":
                user = new Staff(username, encodedPassword);
                break;
            default:
                throw new IllegalArgumentException("Invalid user type: " + userType);
        }

        userRepository.save(user);
        System.out.println("Super user '" + username + "' with type '" + userType + "' created successfully.");
    }

    // Metode opsional jika Anda perlu mengambil User entity secara langsung dari Service
    public Optional<User> findUserEntityByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override // Penting: Ini implementasi dari interface UserService
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}