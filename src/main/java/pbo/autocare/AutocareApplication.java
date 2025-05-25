package pbo.autocare;

import pbo.autocare.service.UserServiceImpl;
import pbo.autocare.repository.UserRepository; // Pastikan ini diimport
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AutocareApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutocareApplication.class, args);
    }

    @Bean
    public CommandLineRunner demoData(UserServiceImpl userService, UserRepository userRepository) { // UserRepository DIINJEK DI SINI
        return args -> {
            // Membuat superakun jika belum ada
            if (!userRepository.existsByUsername("admin")) { // PERBAIKAN DI SINI
                userService.createSuperUser("admin", "adminpass", "ADMIN");
            }
            if (!userRepository.existsByUsername("teknisi1")) { // PERBAIKAN DI SINI
                userService.createSuperUser("teknisi1", "teknisipass", "TECHNICIAN");
            }
            if (!userRepository.existsByUsername("staff1")) { // PERBAIKAN DI SINI
                userService.createSuperUser("staff1", "staffpass", "STAFF");
            }
            // Customer akan register sendiri via form
        };
    }
}