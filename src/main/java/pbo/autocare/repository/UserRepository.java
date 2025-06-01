package pbo.autocare.repository;
import pbo.autocare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username); // Tambahkan ini
    
    @Query(value = "SELECT * FROM users WHERE user_type = ?1", nativeQuery = true)
    List<User> findByUserType(String userType);

}