package pe.edu.upc.profile.services.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.profile.entities.Resident;
import pe.edu.upc.profile.repositories.ResidentRepository;
import pe.edu.upc.profile.repositories.UserRepository;
import pe.edu.upc.profile.services.ResidentService;

import java.util.List;
import java.util.Optional;

@Service
public class ResidentServiceImpl implements ResidentService {

    @Autowired
    private ResidentRepository residentRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Resident save(Resident entity) throws Exception {
        return residentRepository.save(entity);
    }

    @Override
    public List<Resident> findAll() throws Exception {
        return residentRepository.findAll();
    }

    @Override
    public Optional<Resident> findById(Long aLong) throws Exception {
        return residentRepository.findById(aLong);
    }

    @Override
    public Resident update(Resident entity) throws Exception {
        return residentRepository.save(entity);
    }

    @Override
    public void deleteById(Long aLong) throws Exception {
        residentRepository.deleteById(aLong);
    }

    @Override
    public Optional<Resident> auth(String email, String password) {
        return residentRepository.auth(email, password);
    }

    @Override
    public Optional<Integer> authToken(String token) {
        return residentRepository.authToken(token);
    }
}
