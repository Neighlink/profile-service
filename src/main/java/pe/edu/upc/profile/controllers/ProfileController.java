package pe.edu.upc.profile.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.profile.entities.*;
import pe.edu.upc.profile.models.*;
import pe.edu.upc.profile.services.*;

import javax.websocket.server.PathParam;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.logging.Logger;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final static String URL_PROFILE = "http://localhost:8091/configurations";

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
    @Autowired
    private CondominiumRuleService condominiumRuleService;

    private ResponseDepartment getDepartmentByCode(String token, String code) {
        try {
            var values = new HashMap<String, String>();
            values.put("code", code);
            var objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(values);
            String url = URL_PROFILE + "/departments";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .setHeader("Authorization", token)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject responseAPI = new JSONObject(response.body());
            var status = responseAPI.getInt("status");
            if (status != 200) {
                ResponseDepartment department = new ResponseDepartment();
                department.setId(responseAPI.getLong("id"));
                department.setBuildingId(responseAPI.getLong("buildingId"));
                department.setLimiteRegister(responseAPI.getInt("limiteRegister"));
                department.setName(responseAPI.getString("name"));
                department.setSecretCode(responseAPI.getString("secretCode"));
                return department;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

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

    public void conflictResponse(String message) {
        response.setStatus(HttpStatus.CONFLICT.value());
        response.setMessage(message);
        status = HttpStatus.CONFLICT;
    }

    public void internalServerErrorResponse(String message) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase() + " => " + message);
    }

    // API PARA AUTORIZAR EN OTROS SERVICIOS
    @PostMapping(path = "/authToken", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> authToken(@RequestHeader String Authorization) {
        response = new Response();
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

    // START LOGIN
    @PostMapping(path = "/administrators/auth", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> authAdministrator(@RequestBody UserAuth auth) {
        response = new Response();
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
        response = new Response();
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

    @PostMapping(path = "/users/auth", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> login(@RequestBody UserAuth auth) {
        response = new Response();
        try {
            Optional<Resident> resident = residentService.auth(auth.getEmail(), auth.getPassword());
            Optional<Administrator> administrator = administratorService.auth(auth.getEmail(), auth.getPassword());
            if (resident.isEmpty() && administrator.isEmpty()) {
                notFoundResponse();
            } else {
                if (resident.isEmpty()) {
                    ResponseAuthLogin response = new ResponseAuthLogin();
                    response.setUser(administrator);
                    response.setUserType("ADMINISTRADOR");
                    okResponse(response);
                } else {
                    ResponseAuthLogin response = new ResponseAuthLogin();
                    response.setUser(resident);
                    response.setUserType("RESIDENTE");
                    okResponse(response);
                }
            }
            return new ResponseEntity<>(response, status);
        } catch (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }
    // END LOGIN

    // INFO RESIDENTE
    @GetMapping(path = "/residents/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> geResidentProfile(@PathVariable("id") Long id, @RequestHeader String Authorization) {
        response = new Response();
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

    // INFO ADMINISTRADOR
    @GetMapping(path = "/administrators/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getAdministratorProfile(@PathVariable("id") Long id, @RequestHeader String Authorization) {
        response = new Response();
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

    // INFO PLAN DE MEMBRESIA DE ADMINISTRADOR
    @GetMapping(path = "/administrators/{id}/planMembers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getAllPlanMemberByAdministrator(@PathVariable("id") Long id, @RequestHeader String Authorization) {
        response = new Response();
        try {
            Optional<Integer> adminId = administratorService.authToken(Authorization);
            if (adminId.isEmpty()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            if (!Long.valueOf(adminId.get()).equals(id)) {
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

    // INFO DEL PLAN
    @GetMapping(path = "/administrators/{adminId}/planMembers/{planId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getPlanMemberById(@PathVariable("adminId") Long adminId, @PathVariable("planId") Long planId, @RequestHeader String Authorization) {
        response = new Response();
        try {
            Optional<Integer> authAdminId = administratorService.authToken(Authorization);
            if (authAdminId.isEmpty()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }

            if (!Long.valueOf(authAdminId.get()).equals(adminId)) {
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

    // COMIENZA CONDOMINIOS
    @GetMapping(path = "/administrators/{adminId}/condominiums", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getCondominiumsByAdmin(@PathVariable("adminId") Long adminId, @RequestHeader String Authorization) {
        response = new Response();
        try {
            Optional<Integer> authAdminId = administratorService.authToken(Authorization);
            if (authAdminId.isEmpty()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            if (!Long.valueOf(authAdminId.get()).equals(adminId)) {
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

    @PostMapping(path = "/condominiums", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> postBuildinfByCondominium(@RequestHeader String Authorization, @RequestBody RequestCondominium requestCondominium) {
        response = new Response();
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
    public ResponseEntity<Response> updateCondominium(@PathVariable("condominiumId") Long condominiumId, @RequestHeader String Authorization, @RequestBody RequestCondominium requestCondominium) {
        response = new Response();
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
    public ResponseEntity<Response> deleteDepartmentsByCondominium(@PathVariable("condominiumId") Long condominiumId, @RequestHeader String Authorization) {
        response = new Response();
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
    // TERMINA CONDOMINIOS

    // EMPIZA CRUD REGLAS DE CONDOMINIO
    @GetMapping(path = "/condominiums/{condominiumId}/condominiumrules", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getRulesByCondominium(@PathVariable("condominiumId") Long condominiumId, @RequestHeader String Authorization) {
        response = new Response();
        try {
            Optional<Integer> adminId = administratorService.authToken(Authorization);
            Optional<Integer> residentId = residentService.authToken(Authorization);
            if (adminId.isEmpty() && residentId.isEmpty()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            List<CondominiumRule> condominiumRules = condominiumRuleService.findAllByCoddominium(condominiumId);
            okResponse(condominiumRules);
            return new ResponseEntity<>(response, status);
        } catch (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @PostMapping(path = "/condominiums/{condominiumId}/condominiumrules", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> addCondominiumRule(@PathVariable("condominiumId") Long condominiumId, @RequestHeader String Authorization, @RequestBody RequestCondominiumRule requestCondominiumRule) {
        response = new Response();
        try {
            Optional<Integer> adminId = administratorService.authToken(Authorization);
            if (adminId.isEmpty()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            CondominiumRule condominiumRule = new CondominiumRule();
            condominiumRule.setName(requestCondominiumRule.getNamme());
            condominiumRule.setDescription(requestCondominiumRule.getDescription());
            condominiumRule.setCondominiumId(condominiumId);
            CondominiumRule condominiumRuleSaved = condominiumRuleService.save(condominiumRule);
            okResponse(condominiumRuleSaved);
            return new ResponseEntity<>(response, status);
        } catch (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @PutMapping(path = "/condominiums/{condominiumId}/condominiumrules/{condominiumRuleId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> updateCondominiumRule(@PathVariable("condominiumId") Long condominiumId, @PathVariable("condominiumRuleId") Long condominiumRuleId, @RequestHeader String Authorization, @RequestBody RequestCondominiumRule requestCondominiumRule) {
        response = new Response();
        try {
            Optional<Integer> adminId = administratorService.authToken(Authorization);
            if (adminId.isEmpty()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            Optional<CondominiumRule> condominiumRule = condominiumRuleService.findById(condominiumRuleId);
            if (condominiumRule.isEmpty()) {
                notFoundResponse();
            } else {
                condominiumRule.get().setName(requestCondominiumRule.getNamme());
                condominiumRule.get().setDescription(requestCondominiumRule.getDescription());
                CondominiumRule condominiumRuleSaved = condominiumRuleService.save(condominiumRule.get());
                okResponse(condominiumRuleSaved);
            }
            return new ResponseEntity<>(response, status);
        } catch (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @DeleteMapping(path = "/condominiums/{condominiumId}/condominiumrules/{condominiumRuleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> deleteCondominiumRule(@PathVariable("condominiumId") Long condominiumId, @PathVariable("condominiumRuleId") Long condominiumRuleId, @RequestHeader String Authorization) {
        response = new Response();
        try {
            Optional<Integer> adminId = administratorService.authToken(Authorization);
            Optional<Integer> residentId = residentService.authToken(Authorization);
            if (adminId.isEmpty() && residentId.isEmpty()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            Optional<CondominiumRule> condominiumRule = condominiumRuleService.findById(condominiumRuleId);
            if (condominiumRule.isEmpty()) {
                notFoundResponse();
            } else {
                condominiumRuleService.deleteById(condominiumRuleId);
                okResponse(null);
            }
            return new ResponseEntity<>(response, status);
        } catch (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }
    // TERMINA CRUD REGLAS DE CONDOMINIO

    // EMPIEZA REGISTRAR USUARIO ADMIN Y RESIDENTE
    @PostMapping(path = "/administrators", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> createAdministrator(@RequestBody RequestUser requestUser) {
        response = new Response();
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
        response = new Response();
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
    // TERMINA REGISTRAR USUARIO ADMIN Y RESIDENTE

    // EMPIEZA CRUD DE RESIDENTES DE CONDOMINIO
    @GetMapping(path = "/residentdepartments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getResidentsByCondominium(@PathParam("condominiumId") Long condominiumId, @PathParam("departmentId") Long departmentId, @RequestHeader String Authorization) {
        response = new Response();
        try {
            LOGGER.info("data => " + condominiumId + " - - " + departmentId);
            Optional<Integer> adminId = administratorService.authToken(Authorization);
            if (adminId.isEmpty()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }

            if (condominiumId == null && departmentId == null) {
                conflictResponse("filtro necesario de condominiumId o departmentId");
            } else {
                List<ResidentDepartment> residentDepartments;
                if (condominiumId != null) {
                    residentDepartments = residentDepartmentService.findAllByCondominium(condominiumId);
                } else {
                    residentDepartments = residentDepartmentService.findAllByDepartment(departmentId);
                }
                okResponse(residentDepartments);
            }
            return new ResponseEntity<>(response, status);
        } catch (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @PostMapping(path = "/residentdepartments", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> addResidentDepartment(@RequestHeader String Authorization, @RequestBody RequestResidentdepartment requestResidentdepartment) {
        response = new Response();
        try {
            Optional<Integer> resident = residentService.authToken(Authorization);
            if (resident.isEmpty()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            ResponseDepartment responseDepartment = getDepartmentByCode(Authorization, requestResidentdepartment.getCode());
            if (responseDepartment == null) {
                notFoundResponse();
                return new ResponseEntity<>(response, status);
            }
            ResidentDepartment residentDepartment = new ResidentDepartment();
            residentDepartment.setDepartmentId(responseDepartment.getId());
            residentDepartment.setBuildingId(responseDepartment.getBuildingId());
            residentDepartment.setCondominiumId(responseDepartment.getCondominiumId());
            residentDepartment.setDelete(false);
            residentDepartment.setResidentId(Long.valueOf(resident.get()));
            okResponse(null);
            return new ResponseEntity<>(response, status);
        } catch (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }

    @DeleteMapping(path = "/residentdepartments/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> deleteResidentDepartment(@PathVariable("id") Long id, @RequestHeader String Authorization) {
        response = new Response();
        try {
            Optional<Integer> adminId = administratorService.authToken(Authorization);
            if (adminId.isEmpty()) {
                unauthorizedResponse();
                return new ResponseEntity<>(response, status);
            }
            residentDepartmentService.deleteById(id);
            okResponse(null);
            return new ResponseEntity<>(response, status);
        } catch (Exception e) {
            internalServerErrorResponse(e.getMessage());
            return new ResponseEntity<>(response, status);
        }
    }
    // TERMINAL CRUD DE RESIDENTES DE CONDOMINIO
}
