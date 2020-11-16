package pe.edu.upc.profile.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.profile.entities.CondominiumRule;

import java.util.List;

@Repository
public interface CondominiumRuleRepository extends JpaRepository<CondominiumRule, Long> {
    @Query("SELECT c FROM CondominiumRule c WHERE c.condominiumId = :condominiumId")
    List<CondominiumRule> getRulesByCondominium(@Param("condominiumId") Long condominiumId);
}
