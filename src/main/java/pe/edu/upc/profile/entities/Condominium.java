package pe.edu.upc.profile.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name ="condominiums")
@Data
public class Condominium {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private Long AdministratorId;
}
