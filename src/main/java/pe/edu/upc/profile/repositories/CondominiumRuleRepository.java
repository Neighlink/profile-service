package pe.edu.upc.profile.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.profile.entities.CondominiuRule;

import java.util.List;
import java.util.Optional;

@Repository
public interface CondominiumRuleRepository extends JpaRepository<CondominiuRule, Long> {
    @Query("SELECT c FROM CondominiuRule c WHERE c.condominiumId = :condominiumId")
    Optional<List<CondominiuRule>> getRulesByCondominium(@Param("condominiumId") Long condominiumId);
}
