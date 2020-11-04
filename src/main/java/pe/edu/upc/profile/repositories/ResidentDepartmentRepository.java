package pe.edu.upc.profile.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.profile.entities.ResidentDepartment;

import java.util.List;
import java.util.Optional;

public interface ResidentDepartmentRepository extends JpaRepository<ResidentDepartment, Long> {
    @Query("SELECT r FROM ResidentDepartment r WHERE r.residentId = :residentId")
    Optional<List<ResidentDepartment>> findAllByResidentId(@Param("residentId") Long residentId);
    @Query("UPDATE ResidentDepartment r SET r.isDelete = true WHERE r.condominiumId = :condominiumId")
    void deleteAllByCondominiumId(@Param("condominiumId") Long condominiumId);
}
