package pe.edu.upc.profile.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "administrators")
@Data
public class Administrator implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "userId")
    private User user;
    @Column(nullable = false)
    private boolean isBlocked;
    @Column(nullable = false)
    private boolean planActivated;
}
