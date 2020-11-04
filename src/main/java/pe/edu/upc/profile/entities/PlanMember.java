package pe.edu.upc.profile.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "planMember")
@Data
public class PlanMember {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private Date dateCreated;
    @Column()
    @Temporal(TemporalType.TIMESTAMP)
    private Date datePayed;
    @Column(nullable = false)
    private Float totalPrice;
    @Column(nullable = false)
    private boolean isPayed;
    @Column(nullable = false)
    private Long administratorId;
}
