package pe.edu.upc.profile.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="condominiumRules")
@Data
public class CondominiumRule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Long condominiumId;
}
