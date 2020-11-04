package pe.edu.upc.profile.services;

import pe.edu.upc.profile.entities.ResidentDepartment;

import java.util.List;
import java.util.Optional;

public interface ResidentDepartmentService extends CrudService<ResidentDepartment, Long>{
    Optional<List<ResidentDepartment>> findAllByResidentId( Long residentId);
    void deleteAllByCondominiumId( Long condominiumId);
}
