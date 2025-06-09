package pbo.autocare.config; // Pastikan package name Anda benar

import pbo.autocare.service.UserServiceImpl; // Import UserServiceImpl
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // HAPUS INJEKSI UserServiceImpl DARI CONSTRUCTOR
    // private final UserServiceImpl userService;
    // public SecurityConfig(UserServiceImpl userService) {
    //     this.userService = userService;
    // }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Mengkonfigurasi AuthenticationProvider untuk menggunakan UserDetailsService kustom
    // Injeksi UserServiceImpl dilakukan LANGSUNG DI PARAMETER METHOD BEAN ini
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserServiceImpl userService) { // UserServiceImpl DIINJEK DI SINI
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService); // Menggunakan UserServiceImpl sebagai UserDetailsService
        auth.setPasswordEncoder(passwordEncoder()); // Menggunakan PasswordEncoder yang sudah didefinisikan
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserServiceImpl userService) throws Exception {
        http
            .authenticationProvider(authenticationProvider(userService))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/img/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/technician/**").hasRole("TECHNICIAN")
                .requestMatchers("/staff/**").hasRole("STAFF")
                .requestMatchers("/customer/**").hasRole("CUSTOMER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(authenticationSuccessHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }


    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                response.sendRedirect("/admin/dashboard");
            } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TECHNICIAN"))) {
                response.sendRedirect("/technician/dashboard");
            } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"))) {
                response.sendRedirect("/staff/dashboard");
            } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"))) {
                response.sendRedirect("/customer/dashboard");
            } else {
                response.sendRedirect("/"); // Fallback ke landing page
            }
        };
    }
}