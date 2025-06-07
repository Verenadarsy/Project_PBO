package pbo.autocare.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pbo.autocare.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    // --- PENTING: Metode ini HARUS ada untuk findByEmail di UserServiceImpl ---
    Optional<User> findByEmail(String email);
    // -----------------------------------------------------------------------

    @Query(value = "SELECT * FROM users WHERE user_type = ?1", nativeQuery = true)
    List<User> findByUserType(String userType);
}