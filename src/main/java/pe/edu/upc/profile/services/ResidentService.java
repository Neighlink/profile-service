package pe.edu.upc.profile.services;

import pe.edu.upc.profile.entities.Resident;

import java.util.Optional;

public interface ResidentService extends CrudService<Resident, Long> {
    Optional<Resident> auth(String email, String password);
    Optional<Integer> authToken(String token);
}
