package tn.esprit.autoloc.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "vehicule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 20)
    private String immatriculation;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String marque;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String modele;

    @Min(1900)
    @Column(nullable = false)
    private Integer annee;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategorieVehicule categorie;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutVehicule statut;

    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prixParJour;

    @Column(length = 500)
    private String description;
}