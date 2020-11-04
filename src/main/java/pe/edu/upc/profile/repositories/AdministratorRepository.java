package pe.edu.upc.profile.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.profile.entities.Administrator;

import java.util.Optional;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    @Query("SELECT a FROM Administrator a WHERE a.user.email = :user AND a.user.password = :password")
    Optional<Administrator> auth(@Param("user") String email, @Param("password") String password);

    @Query("SELECT a.id FROM Administrator a WHERE a.user.token = :token")
    Optional<Integer> authToken(@Param("token") String token);
}
