package tn.esprit.autoloc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;

@Entity
@Table(name = "employe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEmploye;

    @Column(nullable = false, length = 50)
    private String nom;

    @Column(nullable = false, length = 50)
    private String prenom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoleEmploye role;

    @ManyToOne
    @JsonIgnoreProperties("employes")
    private Agence agence;

    public void assignAgence(Agence agence) {
        this.agence = agence;
        if (agence != null) {
            if (agence.getEmployes() == null) {
                agence.setEmployes(new HashSet<>());
            }
            agence.getEmployes().add(this);
        }
    }
}
