package pe.edu.upc.profile.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.profile.entities.Condominium;

import java.util.List;
import java.util.Optional;

@Repository
public interface CondominiumRepository extends JpaRepository<Condominium, Long> {
    @Query("SELECT c FROM Condominium c WHERE c.AdministratorId = :adminId AND c.isDelete = false")
    Optional<List<Condominium>> getCondominiumsByAdmin(@Param("adminId") Long adminId);
}
