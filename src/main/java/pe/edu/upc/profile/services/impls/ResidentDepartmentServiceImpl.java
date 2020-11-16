package pe.edu.upc.profile.services.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.profile.entities.ResidentDepartment;
import pe.edu.upc.profile.repositories.ResidentDepartmentRepository;
import pe.edu.upc.profile.services.ResidentDepartmentService;

import java.util.List;
import java.util.Optional;

@Service
public class ResidentDepartmentServiceImpl implements ResidentDepartmentService {

    @Autowired
    private ResidentDepartmentRepository residentDepartmentRepository;

    @Override
    public ResidentDepartment save(ResidentDepartment entity) throws Exception {
        return residentDepartmentRepository.save(entity);
    }

    @Override
    public List<ResidentDepartment> findAll() throws Exception {
        return residentDepartmentRepository.findAll();
    }

    @Override
    public Optional<ResidentDepartment> findById(Long aLong) throws Exception {
        return residentDepartmentRepository.findById(aLong);
    }

    @Override
    public ResidentDepartment update(ResidentDepartment entity) throws Exception {
        return residentDepartmentRepository.save(entity);
    }

    @Override
    public void deleteById(Long aLong) throws Exception {
        residentDepartmentRepository.deleteById(aLong);
    }

    @Override
    public Optional<List<ResidentDepartment>> findAllByResidentId(Long residentId) {
        return residentDepartmentRepository.findAllByResidentId(residentId);
    }

    @Override
    public void deleteAllByCondominiumId(Long condominiumId) {
        residentDepartmentRepository.deleteAllByCondominiumId(condominiumId);
    }

    @Override
    public List<ResidentDepartment> findAllByDepartment(Long departmentId) {
        return residentDepartmentRepository.findAllByDepartment(departmentId);
    }

    @Override
    public List<ResidentDepartment> findAllByCondominium(Long condominiumId) {
        return residentDepartmentRepository.findAllByCondominium(condominiumId);
    }
}
