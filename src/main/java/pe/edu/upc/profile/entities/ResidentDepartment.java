package pe.edu.upc.profile.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "resident_department")
public class ResidentDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(nullable = false)
    private Long departmentId;
    @Column(nullable = false)
    private Long condominiumId;
    @Column(nullable = false)
    private Long buildingId;
    @Column(nullable = false)
    private boolean isDelete;
    @Column(nullable = false)
    private Long residentId;
}
