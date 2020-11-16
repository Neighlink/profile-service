package pe.edu.upc.profile.services;

import pe.edu.upc.profile.entities.CondominiumRule;

import java.util.List;

public interface CondominiumRuleService extends CrudService<CondominiumRule, Long> {
    List<CondominiumRule> findAllByCoddominium(Long condominiumId);
}
