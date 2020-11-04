package pe.edu.upc.profile.services;

import pe.edu.upc.profile.entities.Administrator;
import pe.edu.upc.profile.entities.PlanMember;

import java.util.List;
import java.util.Optional;

public interface AdministratorService extends CrudService<Administrator, Long> {
    Optional<Administrator> auth(String email, String password) throws Exception;

    Optional<Integer> authToken(String token) throws Exception;
    Optional<List<PlanMember>> getPlanMemberByAdminId(Long id) throws Exception;
    Optional<PlanMember> getPlanMemberById(Long id) throws Exception;
}
