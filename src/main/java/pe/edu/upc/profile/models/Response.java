package pe.edu.upc.profile.models;


import lombok.Data;

@Data
public class Response {
    private int status;
    private String message = "";
    private Object result;
}
