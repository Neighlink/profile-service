package pe.edu.upc.profile.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.profile.entities.*;
import pe.edu.upc.profile.models.*;
import pe.edu.upc.profile.services.*;

import javax.print.attribute.standard.Media;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    // TODO: Implementar los servicios de registro tanto admin como residnete integrar UUID para el token de usuario

    ProfileController() {
        response = new Response();
    }

    @Autowired
    private AdministratorService administratorService;
    @Autowired
    private ResidentService residentService;
    @Autowired
    private CondominiumService condominiumService;
    @Autowired
    private ResidentDepartmentService residentDepartmentService;
    @Autowired
    private UserService userService;


    private final static Logger LOGGER = Logger.getLogger("bitacora.subnivel.Control");
    Response response = new Response();
    HttpStatus status;

    public void unauthorizedResponse() {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setMessage("UNAUTHORIZED USER");
        status = HttpStatus.UNAUTHORIZED;
    }

    public void notFoundResponse() {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setMessage("ENTITY NOT FOUND");
        status = HttpStatus.NOT_FOUND;
    }

    public void okResponse(Object result) {
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("SERVICE SUCCESS");
        response.setResult(result);
        status = HttpStatus.OK;
    }

    public void internalServerErrorResponse(String message) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase() + " => " + message);
    }

    @PostMapping(path = "/authToken", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> authToken(@RequestHeader String Authorization) {
        ResponseAuth responseAuth = new ResponseAuth();
        response = new Response();
        try {
            Optional<Integer> adminId = administratorService.authToken(Authorization);
            Optional<Integer> residentId = residentService.authToken(Authorization);
            if (adminId.isEmpty() && residentId.isEmpty()) {
                responseAuth.setAuthorized(false);
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            responseAuth.setAuthorized(true);
            if (!adminId.isEmpty()) {
                responseAuth.setId(Long.valueOf(adminId.get()));
                responseAuth.setUserType("ADM");
                okResponse(responseAuth);
            } else {
                responseAuth.setId(Long.valueOf(residentId.get()));
                responseAuth.setUserType("RES");
                okResponse(responseAuth);
            }
            return new ResponseEntity<>(response, status);
        } catch (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }


    @PostMapping(path = "/administrators/auth", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> authAdministrator(@RequestBody UserAuth auth) {
        try {
            Optional<Administrator> administrator = administratorService.auth(auth.getEmail(), auth.getPassword());
            if (administrator.isEmpty()) {
                notFoundResponse();
            } else {
                okResponse(administrator.get());
            }
            return new ResponseEntity<>(response, status);
        } catch (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @PostMapping(path = "/residents/auth", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> authResident(@RequestBody UserAuth auth) {
        try {
            Optional<Resident> resident = residentService.auth(auth.getEmail(), auth.getPassword());
            if (resident.isEmpty()) {
                notFoundResponse();
            } else {
                okResponse(resident.get());
            }
            return new ResponseEntity<>(response, status);
        } catch (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @GetMapping(path = "/residents/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> geResidentProfile(@PathVariable("id") Long id, @RequestHeader String Authorization) {
        try {
            Optional<Integer> adminId = administratorService.authToken(Authorization);
            Optional<Integer> residentId = residentService.authToken(Authorization);
            if (adminId.isEmpty() && residentId.isEmpty()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            Optional<Resident> resident = residentService.findById(id);
            if (resident.isEmpty()) {
                notFoundResponse();
            } else {
                if (resident.get().getId().equals(id) || !adminId.isEmpty()) {
                    okResponse(resident.get());
                } else {
                    unauthorizedResponse();
                }
            }
            return new ResponseEntity<>(response, status);
        } catch (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @GetMapping(path = "/administrators/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getAdministratorProfile(@PathVariable("id") Long id, @RequestHeader String Authorization) {
        try {
            Optional<Integer> adminId = administratorService.authToken(Authorization);
            if (adminId.isEmpty()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            Optional<Administrator> administrator = administratorService.findById(id);
            if (administrator.isEmpty()) {
                notFoundResponse();
            } else {
                if (administrator.get().getId().equals(id)) {
                    okResponse(administrator.get());
                } else {
                    unauthorizedResponse();
                }
            }
            return new ResponseEntity<>(response, status);
        } catch (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @GetMapping(path = "/administrators/{id}/planMembers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getAllPlanMemberByAdministrator(@PathVariable("id") Long id, @RequestHeader String Authorization) {
        try {
            Optional<Integer> adminId = administratorService.authToken(Authorization);
            if (adminId.isEmpty() || !adminId.equals(id)) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            Optional<List<PlanMember>> planMembers = administratorService.getPlanMemberByAdminId(id);
            if (planMembers.isEmpty()) {
                notFoundResponse();
            } else {
                okResponse(planMembers.get());
            }
            return new ResponseEntity<>(response, status);
        } catch (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @GetMapping(path = "/administrators/{adminId}/planMembers/{planId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getPlanMemberById(@PathVariable("adminId") Long adminId, @PathVariable("planId") Long planId, @RequestHeader String Authorization) {
        try {
            Optional<Integer> authAdminId = administratorService.authToken(Authorization);
            if (authAdminId.isEmpty() || !authAdminId.equals(adminId)) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            Optional<PlanMember> planMember = administratorService.getPlanMemberById(planId);
            if (planMember.isEmpty()) {
                notFoundResponse();
            } else {
                if (!planMember.get().getAdministratorId().equals(adminId)) {
                    unauthorizedResponse();
                } else {
                    okResponse(planMember.get());
                }
            }
            return new ResponseEntity<>(response, status);
        } catch (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @GetMapping(path = "/administrators/{adminId}/condominiums", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getCondominiumsByAdmin(@PathVariable("adminId") Long adminId, @RequestHeader String Authorization) {
        try {
            Optional<Integer> authAdminId = administratorService.authToken(Authorization);
            if (authAdminId.isEmpty() || !authAdminId.equals(adminId)) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            Optional<List<Condominium>> condominiums = condominiumService.getCondominiumByAdmin(adminId);
            if (condominiums.isEmpty()) {
                notFoundResponse();
            } else {
                okResponse(condominiums.get());
            }
            return new ResponseEntity<>(response, status);
        } catch (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @GetMapping(path = "/condominiums/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getRulesByCondominium(@PathVariable("id") Long condominiumId, @RequestHeader String Authorization) {
        try {
            Optional<Integer> adminId = administratorService.authToken(Authorization);
            Optional<Integer> residentId = residentService.authToken(Authorization);
            if (adminId.isEmpty() && residentId.isEmpty()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            Optional<List<CondominiuRule>> condominiumRules = condominiumService.getRulesByCondominium(condominiumId);
            if (condominiumRules.isEmpty()) {
                notFoundResponse();
            } else {
                okResponse(condominiumRules.get());
            }
            return new ResponseEntity<>(response, status);
        } catch (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @PostMapping(path = "/condominiums", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> postBuildinfByCondominium(@RequestHeader String Authorization, @RequestBody RequestCondominium requestCondominium) {
        try {
            Optional<Integer> adminId = administratorService.authToken(Authorization);
            if (adminId.isEmpty()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            Condominium condominium = new Condominium();
            condominium.setAddress(requestCondominium.getAddress());
            condominium.setName(requestCondominium.getName());
            condominium.setDescription(requestCondominium.getDescription());
            condominium.setAdministratorId(Long.valueOf(adminId.get()));
            Condominium condominiumSaved = condominiumService.save(condominium);
            okResponse(condominiumSaved);
            return new ResponseEntity<>(response, status);
        } catch
        (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @PutMapping(path = "/condominiums/{condominiumId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> updateDepartmentsByBuilding(@RequestParam("condominiumId") Long condominiumId, @RequestHeader String Authorization, @RequestBody RequestCondominium requestCondominium) {
        try {
            Optional<Integer> adminId = administratorService.authToken(Authorization);
            if (adminId.isEmpty()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }

            Optional<Condominium> condominium = condominiumService.findById(condominiumId);
            if (condominium.isEmpty()) {
                notFoundResponse();
                return new ResponseEntity<>(response, status);
            }
            if (!requestCondominium.getAddress().isEmpty())
                condominium.get().setAddress(requestCondominium.getAddress());
            if (!requestCondominium.getName().isEmpty())
                condominium.get().setName(requestCondominium.getName());
            if (!requestCondominium.getDescription().isEmpty())
                condominium.get().setDescription(requestCondominium.getDescription());
            Condominium condominiumSaved = condominiumService.save(condominium.get());
            okResponse(condominiumSaved);
            return new ResponseEntity<>(response, status);
        } catch
        (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @DeleteMapping(path = "/condominiums/{condominiumId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> deleteDepartmentsByCondominium(@RequestParam("condominiumId") Long condominiumId, @RequestHeader String Authorization) {
        try {
            Optional<Integer> adminId = administratorService.authToken(Authorization);
            if (adminId.isEmpty()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }

            Optional<Condominium> condominium = condominiumService.findById(condominiumId);
            if (condominium.isEmpty()) {
                notFoundResponse();
                return new ResponseEntity<>(response, status);
            }
            condominium.get().setDelete(true);
            condominiumService.save(condominium.get());
            residentDepartmentService.deleteAllByCondominiumId(condominiumId);
            okResponse(null);
            return new ResponseEntity<>(response, status);
        } catch
        (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @PostMapping(path = "/administrators", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> createAdministrator(@RequestBody RequestUser requestUser) {
        try {
            User user = new User();
            user.setEmail(requestUser.getEmail());
            user.setBirthDate(requestUser.getBirthDate());
            user.setGender(requestUser.getGender());
            user.setPassword(requestUser.getPassword());
            user.setName(requestUser.getName());
            user.setLastName(requestUser.getLastName());
            user.setPhone(requestUser.getPhone());
            user.setToken(UUID.randomUUID().toString());
            User userSaved = userService.save(user);
            Administrator administrator = new Administrator();
            administrator.setUser(userSaved);
            administrator.setBlocked(false);
            administrator.setPlanActivated(true);
            administratorService.save(administrator);
            okResponse(null);
            return new ResponseEntity<>(response, status);
        } catch
        (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @PostMapping(path = "/residents", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> createResident(@RequestBody RequestUser requestUser) {
        try {
            User user = new User();
            user.setEmail(requestUser.getEmail());
            user.setBirthDate(requestUser.getBirthDate());
            user.setGender(requestUser.getGender());
            user.setPassword(requestUser.getPassword());
            user.setName(requestUser.getName());
            user.setLastName(requestUser.getLastName());
            user.setPhone(requestUser.getPhone());
            user.setToken(UUID.randomUUID().toString());
            User userSaved = userService.save(user);
            Resident resident = new Resident();
            resident.setUser(userSaved);
            resident.setBlocked(false);
            residentService.save(resident);
            okResponse(null);
            return new ResponseEntity<>(response, status);
        } catch
        (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

}
