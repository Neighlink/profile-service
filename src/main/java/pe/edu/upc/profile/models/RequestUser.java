package pe.edu.upc.profile.models;

import lombok.Data;

import java.util.Date;

@Data
public class RequestUser {
    private String name;
    private  String lastName;
    private Date birthDate;
    private String phone;
    private String gender;
    private String email;
    private String password;
}
