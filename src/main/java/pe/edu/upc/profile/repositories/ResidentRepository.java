package pe.edu.upc.profile.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.profile.entities.Administrator;
import pe.edu.upc.profile.entities.Resident;

import java.util.Optional;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, Long> {
    @Query("SELECT r FROM Resident r WHERE r.user.email = :user AND r.user.password = :password")
    Optional<Resident> auth(@Param("user") String email, @Param("password") String password);

    @Query("SELECT r.id FROM Resident r WHERE r.user.token = :token")
    Optional<Integer> authToken(@Param("token") String token);
}
