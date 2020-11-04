package pe.edu.upc.profile.services.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.profile.entities.CondominiuRule;
import pe.edu.upc.profile.entities.Condominium;
import pe.edu.upc.profile.repositories.CondominiumRepository;
import pe.edu.upc.profile.repositories.CondominiumRuleRepository;
import pe.edu.upc.profile.services.CondominiumService;

import java.util.List;
import java.util.Optional;

@Service
public class CondominiumServiceImpl implements CondominiumService {
    @Autowired
    private CondominiumRepository condominiumRepository;
    @Autowired
    private CondominiumRuleRepository condominiumRuleRepository;

    @Override
    public Condominium save(Condominium entity) throws Exception {
        return condominiumRepository.save(entity);
    }

    @Override
    public List<Condominium> findAll() throws Exception {
        return condominiumRepository.findAll();
    }

    @Override
    public Optional<Condominium> findById(Long aLong) throws Exception {
        return condominiumRepository.findById(aLong);
    }

    @Override
    public Condominium update(Condominium entity) throws Exception {
        return condominiumRepository.save(entity);
    }

    @Override
    public void deleteById(Long aLong) throws Exception {
        condominiumRepository.deleteById(aLong);
    }


    @Override
    public Optional<List<Condominium>> getCondominiumByAdmin(Long adminId) {
        return condominiumRepository.getCondominiumsByAdmin(adminId);
    }

    @Override
    public Optional<List<CondominiuRule>> getRulesByCondominium(Long condominiumId) {
        return condominiumRuleRepository.getRulesByCondominium(condominiumId);
    }
}
