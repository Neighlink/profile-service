package pe.edu.upc.profile.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "residents")
@Data
public class Resident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //@Column(nullable = false)
    //private Long userId;
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "userId")
    private User user;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private boolean isBlocked;
    @Column(nullable = false)
    private boolean planActivated;
}
