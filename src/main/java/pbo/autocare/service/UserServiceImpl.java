// src/main/java/pbo/autocare/service/UserServiceImpl.java

package pbo.autocare.service;

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
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

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

    @Override
    public Customer registerNewCustomer(Customer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        return userRepository.save(customer);
    }

    public void createSuperUser(String username, String rawPassword, String userType) {
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

    public Optional<User> findUserEntityByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }


    @Override
    public List<User> getAllStaff() {
        // Implementasi logika untuk mendapatkan semua staf di sini
        // Ini akan memanggil metode findByUserType dari repository Anda
        return userRepository.findByUserType("STAFF");
    }

    @Override
    public List<User> getAllTechnicians() {
        // Implementasi logika untuk mendapatkan semua teknisi
        // Asumsi Technician adalah subclass dari User, dan ada user_type "TECHNICIAN"
        return userRepository.findByUserType("TECHNICIAN");
    }
}