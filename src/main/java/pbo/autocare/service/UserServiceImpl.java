package pbo.autocare.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pbo.autocare.dto.CustomerFormDTO; 
import pbo.autocare.model.Admin;
import pbo.autocare.model.Customer;
import pbo.autocare.model.Staff;
import pbo.autocare.model.Technician;
import pbo.autocare.model.User;
import pbo.autocare.repository.ServiceOrderRepository;
import pbo.autocare.repository.UserRepository; 

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @param username Username dari pengguna yang akan dimuat.
     * @return Objek UserDetails yang berisi detail pengguna.
     * @throws UsernameNotFoundException Jika pengguna dengan username yang diberikan tidak ditemukan.
     */
    @Override
    @Transactional(readOnly = true) 
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Mencari pengguna di database berdasarkan username
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

    /**
     * @param customer Objek Customer yang akan didaftarkan.
     * @return Objek Customer yang sudah tersimpan di database.
     */
    @Override
    @Transactional 
    public Customer registerNewCustomer(Customer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword())); 
        return userRepository.save(customer);
    }

    /**
     * @param username Username dari user yang akan dibuat.
     * @param rawPassword Password user (belum di-encode).
     * @param userType Tipe user (contoh: "ADMIN", "TECHNICIAN", "STAFF", "CUSTOMER").
     */
    @Override
    @Transactional 
    public void createSuperUser(String username, String rawPassword, String userType) {

        if (userRepository.existsByUsername(username)) {
            System.out.println("Super user '" + username + "' already exists. Skipping creation.");
            return;
        }

        User user;
        String encodedPassword = passwordEncoder.encode(rawPassword); 
        String defaultEmail = username + "@autocare.com";
        String defaultFullName = "Nama " + userType.substring(0, 1).toUpperCase() + userType.substring(1).toLowerCase();
        String defaultPhoneNumber = "08" + String.format("%010d", System.currentTimeMillis() % 10000000000L); 

        switch (userType.toUpperCase()) {
            case "ADMIN" -> user = new Admin(username, encodedPassword, defaultEmail, defaultFullName, defaultPhoneNumber);
            case "TECHNICIAN" -> user = new Technician(username, encodedPassword, defaultEmail, defaultFullName, defaultPhoneNumber);
            case "STAFF" -> user = new Staff(username, encodedPassword, defaultEmail, defaultFullName, defaultPhoneNumber);
            case "CUSTOMER" -> user = new Customer(username, encodedPassword, defaultEmail, defaultFullName, defaultPhoneNumber);
            default -> throw new IllegalArgumentException("Invalid user type: " + userType);
        }

        userRepository.save(user); 
        System.out.println("Super user '" + username + "' with type '" + userType + "' created successfully.");
    }

    /**
     * @param username Username yang dicari.
     * @return Optional yang berisi objek User jika ditemukan, kosong jika tidak.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserEntityByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * @param username Username yang dicari.
     * @return Optional yang berisi objek User jika ditemukan, kosong jika tidak.
     */
    @Override
    @Transactional(readOnly = true) 
    public Optional<User> findUserByUsername(String username) {
        return findUserEntityByUsername(username); 
    }

    /**
     * @param username Username yang akan diperiksa.
     * @return true jika username sudah ada, false jika belum.
     */
    @Override
    @Transactional(readOnly = true) 
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Mengambil daftar semua user dengan peran Staff.
     * @return List objek User yang bertipe Staff.
     */
    @Override
    @Transactional(readOnly = true) 
    public List<User> getAllStaff() {
        return userRepository.findByUserType("STAFF");
    }

    /**
     * Mengambil daftar semua user dengan peran Technician.
     * @return List objek User yang bertipe Technician.
     */
    @Override
    @Transactional(readOnly = true) 
    public List<User> getAllTechnicians() {
        return userRepository.findByUserType("TECHNICIAN");
    }

    /**
     * Mengambil daftar semua user dengan peran Customer.
     * @return List objek Customer.
     */
    @Override
    @Transactional(readOnly = true) 
    public List<Customer> getAllCustomers() {
        return userRepository.findByUserType("CUSTOMER")
                             .stream()
                             .filter(user -> user instanceof Customer) 
                             .map(user -> (Customer) user) 
                             .collect(Collectors.toList()); 
    }

    /**
     * @param CustomerDTO DTO yang berisi data Customer baru.
     * @return Objek Customer yang sudah disimpan.
     * @throws ResponseStatusException Jika username atau email sudah digunakan.
     */
    @Override
    @Transactional 
    public Customer saveNewCustomer(CustomerFormDTO CustomerDTO) {
        // Cek duplikasi username untuk customer baru
        if (userRepository.existsByUsername(CustomerDTO.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username '" + CustomerDTO.getUsername() + "' sudah digunakan. Pilih username lain.");
        }

        if (userRepository.findByEmail(CustomerDTO.getEmail()).isPresent()) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email '" + CustomerDTO.getEmail() + "' sudah terdaftar. Gunakan email lain.");
        }

        Customer customer = new Customer(
            CustomerDTO.getUsername(),
            passwordEncoder.encode(CustomerDTO.getPassword()), 
            CustomerDTO.getEmail(),
            CustomerDTO.getFullName(),
            CustomerDTO.getPhoneNumber()
        );
  
        return userRepository.save(customer); 
    }

    /**
     * Mengambil satu objek Customer berdasarkan ID.
     * @param id ID Customer yang dicari.
     * @return Optional yang berisi objek Customer jika ditemukan dan merupakan Customer.
     */
    @Override
    @Transactional(readOnly = true) // Operasi baca saja.
    public Optional<Customer> getCustomerById(Long id) {

        return userRepository.findById(id)
                             .filter(user -> user instanceof Customer)
                             .map(user -> (Customer) user);
    }

    /**

     * @param id ID Customer yang akan diperbarui.
     * @param CustomerDTO DTO yang berisi data pembaruan.
     * @return Objek Customer yang sudah diperbarui.
     * @throws ResponseStatusException Jika Customer tidak ditemukan, bukan Customer, atau ada duplikasi data.
     */
    @Override
    @Transactional 
    public Customer updateCustomer(Long id, CustomerFormDTO CustomerDTO) {
    
        return userRepository.findById(id)
            .map(existingUser -> {
 
                if (!(existingUser instanceof Customer)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with ID " + id + " is not a Customer.");
                }
                Customer existingCustomer = (Customer) existingUser;


                if (!existingCustomer.getUsername().equals(CustomerDTO.getUsername())) {
                    if (userRepository.existsByUsername(CustomerDTO.getUsername())) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username '" + CustomerDTO.getUsername() + "' sudah digunakan.");
                    }
                }
     
                if (!existingCustomer.getEmail().equals(CustomerDTO.getEmail())) {
                    Optional<User> userWithSameEmail = userRepository.findByEmail(CustomerDTO.getEmail());
                    if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(id)) { // Pastikan email bukan milik user yang sama
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email '" + CustomerDTO.getEmail() + "' sudah terdaftar.");
                    }
                }

                existingCustomer.setUsername(CustomerDTO.getUsername());

                if (CustomerDTO.getPassword() != null && !CustomerDTO.getPassword().isEmpty()) {
                    existingCustomer.setPassword(passwordEncoder.encode(CustomerDTO.getPassword()));
                }
                existingCustomer.setFullName(CustomerDTO.getFullName());
                existingCustomer.setEmail(CustomerDTO.getEmail());
                existingCustomer.setPhoneNumber(CustomerDTO.getPhoneNumber());

                return userRepository.save(existingCustomer); 
            })
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer with ID " + id + " not found."));
    }

    /**
     * Menghapus Customer berdasarkan ID.
     * @param id ID Customer yang akan dihapus.
     * @throws ResponseStatusException Jika Customer tidak ditemukan atau bukan Customer.
     */
    @Override
    @Transactional 
    public void deleteCustomer(Long id) {
        
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty() || !(userOptional.get() instanceof Customer)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer with ID " + id + " not found or is not a Customer.");
        }
        userRepository.deleteById(id); 
    }

    @Override
    public long countCustomers() {
        return userRepository.countByUserType("CUSTOMER"); 
    }

    @Override
    public long countTechnicians() {
        return userRepository.countByUserType("TECHNICIAN");
    }

    @Override
    public long countStaff() {
        return userRepository.countByUserType("STAFF");
    }

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;
    
    public long countOrdersThisMonth() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        Date startDate = Date.from(startOfMonth.atZone(ZoneId.systemDefault()).toInstant());

        return serviceOrderRepository.countByCreatedAtAfter(new Timestamp(startDate.getTime()));
    }

    public Optional<Staff> getStaffById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent() && userOptional.get() instanceof Staff) {
            return Optional.of((Staff) userOptional.get());
        }
        return Optional.empty();
    }
    
    public Staff saveStaff(Staff staff) {

        if (staff.getPassword() != null && !staff.getPassword().isEmpty()) {
            staff.setPassword(passwordEncoder.encode(staff.getPassword()));
        }
        return (Staff) userRepository.save(staff);
    }

    public void deleteStaff(Long id) {
        userRepository.deleteById(id); 
    }

    public List<User> getAllUsers() {
        return userRepository.findAll(); 
    }
}