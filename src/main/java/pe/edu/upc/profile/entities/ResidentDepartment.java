package pe.edu.upc.profile.entities;

import javax.persistence.*;

@Entity
@Table(name = "resident_department")
public class ResidentDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(nullable = false)
    private Long deparmentId;
    @Column(nullable = false)
    private Long condominiumId;
    @Column(nullable = false)
    private Long buildingId;
    @Column(nullable = false)
    private boolean isDelete;
    @Column(nullable = false)
    private Long residentId;
}
