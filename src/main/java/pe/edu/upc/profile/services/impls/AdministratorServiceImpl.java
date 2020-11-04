package pe.edu.upc.profile.services.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.profile.entities.Administrator;
import pe.edu.upc.profile.entities.PlanMember;
import pe.edu.upc.profile.repositories.AdministratorRepository;
import pe.edu.upc.profile.repositories.PlanMemberRepository;
import pe.edu.upc.profile.repositories.UserRepository;
import pe.edu.upc.profile.services.AdministratorService;

import java.util.List;
import java.util.Optional;

@Service
public class AdministratorServiceImpl implements AdministratorService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdministratorRepository administratorRepository;
    @Autowired
    private PlanMemberRepository planMemberRepository;

    @Override
    public Administrator save(Administrator entity) throws Exception {
        return administratorRepository.save(entity);
    }

    @Override
    public List<Administrator> findAll() throws Exception {
        return administratorRepository.findAll();
    }

    @Override
    public Optional<Administrator> findById(Long aLong) throws Exception {
        return administratorRepository.findById(aLong);
    }

    @Override
    public Administrator update(Administrator entity) throws Exception {
        return administratorRepository.save(entity);
    }

    @Override
    public void deleteById(Long aLong) throws Exception {
        administratorRepository.deleteById(aLong);
    }

    @Override
    public Optional<Administrator> auth(String email, String password) throws Exception {
        return administratorRepository.auth(email, password);
    }

    @Override
    public Optional<Integer> authToken(String token) throws Exception {
        return administratorRepository.authToken(token);
    }

    @Override
    public Optional<List<PlanMember>> getPlanMemberByAdminId(Long id) throws Exception {
        return planMemberRepository.getPlanMemberByAdminId(id);
    }

    @Override
    public Optional<PlanMember> getPlanMemberById(Long id) throws Exception {
        return planMemberRepository.findById(id);
    }
}
