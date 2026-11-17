package tn.esprit.autoloc.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "agence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAgence;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 50)
    private String ville;

    @Column(nullable = false, length = 255)
    private String adresse;

    @Column(length = 20)
    private String telephone;

    @Column
    private Integer effectif;

    @JsonIgnore
    @OneToMany(mappedBy = "agence", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Set<Vehicule> vehicules;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "agence")
    private Set<Employe> employes;

    public void addEmploye(Employe employe) {
        if (this.employes == null) {
            this.employes = new HashSet<>();
        }
        this.employes.add(employe);
        if (employe != null) {
            employe.setAgence(this);
        }
    }

    public void removeEmploye(Employe employe) {
        if (this.employes != null && employe != null) {
            this.employes.remove(employe);
            employe.setAgence(null);
        }
    }
}
