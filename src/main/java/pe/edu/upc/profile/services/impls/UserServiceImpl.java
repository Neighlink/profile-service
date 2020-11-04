package pe.edu.upc.profile.services.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.profile.entities.User;
import pe.edu.upc.profile.repositories.UserRepository;
import pe.edu.upc.profile.services.UserService;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User save(User entity) throws Exception {
        return userRepository.save(entity);
    }

    @Override
    public List<User> findAll() throws Exception {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long aLong) throws Exception {
        return userRepository.findById(aLong);
    }

    @Override
    public User update(User entity) throws Exception {
        return userRepository.save(entity);
    }

    @Override
    public void deleteById(Long aLong) throws Exception {
        userRepository.deleteById(aLong);
    }
}
