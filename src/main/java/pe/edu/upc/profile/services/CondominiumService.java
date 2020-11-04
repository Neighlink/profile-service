package pe.edu.upc.profile.services;

import pe.edu.upc.profile.entities.CondominiuRule;
import pe.edu.upc.profile.entities.Condominium;

import java.util.List;
import java.util.Optional;

public interface CondominiumService extends CrudService<Condominium, Long> {
    Optional<List<Condominium>> getCondominiumByAdmin(Long adminId);
    Optional<List<CondominiuRule>> getRulesByCondominium(Long condominiumId);
}
