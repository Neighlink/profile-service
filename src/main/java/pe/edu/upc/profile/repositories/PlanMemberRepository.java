package pe.edu.upc.profile.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.profile.entities.Administrator;
import pe.edu.upc.profile.entities.PlanMember;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanMemberRepository extends JpaRepository<PlanMember, Long> {
    @Query("SELECT p FROM PlanMember p WHERE p.administratorId = :id")
    Optional<List<PlanMember>> getPlanMemberByAdminId(@Param("id") Long id);
}
