package pbo.autocare;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pbo.autocare.repository.UserRepository;
import pbo.autocare.service.UserServiceImpl;

@SpringBootApplication
public class AutocareApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutocareApplication.class, args);
    }
 
    @Bean
    public CommandLineRunner demoData(UserServiceImpl userService, UserRepository userRepository) { 
        return args -> {

            if (!userRepository.existsByUsername("admin")) { 
                userService.createSuperUser("admin", "adminpass", "ADMIN");
            }
            if (!userRepository.existsByUsername("teknisi1")) { 
                userService.createSuperUser("teknisi1", "teknisipass", "TECHNICIAN");
            }
            if (!userRepository.existsByUsername("staff1")) { 
                userService.createSuperUser("staff1", "staffpass", "STAFF");
            }
        };
    }

}
