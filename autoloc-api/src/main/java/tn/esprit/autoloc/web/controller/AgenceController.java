package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.service.ISimulationService;

import java.util.List;

@Tag(name = "Agences", description = "Gestion des agences de location")
@RestController
@RequestMapping("/agences")
@RequiredArgsConstructor
public class AgenceController {

    private final IAgenceService agenceService;
    private final ISimulationService simulationService;

    @Operation(summary = "Lister toutes les agences")
    @ApiResponse(responseCode = "200", description = "Liste des agences récupérée avec succès")
    @GetMapping
    public List<Agence> findAll() {
        return agenceService.findAll();
    }

    @Operation(summary = "Consulter une agence")
    @ApiResponse(responseCode = "200", description = "Agence trouvée")
    @ApiResponse(responseCode = "404", description = "Agence introuvable")
    @GetMapping("/{id}")
    public Agence findById(@Parameter(description = "Identifiant") @PathVariable Long id) {
        return agenceService.findById(id);
    }

    @Operation(summary = "Ajouter une agence")
    @ApiResponse(responseCode = "201", description = "Agence créée")
    @ApiResponse(responseCode = "400", description = "Données invalides ou identifiant déjà fourni")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Agence create(@RequestBody Agence agence) {
        return agenceService.create(agence);
    }

    @Operation(summary = "Modifier une agence")
    @ApiResponse(responseCode = "200", description = "Agence modifiée")
    @ApiResponse(responseCode = "404", description = "Agence introuvable")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @PutMapping("/{id}")
    public Agence update(@Parameter(description = "Identifiant") @PathVariable Long id, @RequestBody Agence agence) {
        return agenceService.update(id, agence);
    }

    @Operation(summary = "Supprimer une agence")
    @ApiResponse(responseCode = "204", description = "Agence supprimée")
    @ApiResponse(responseCode = "404", description = "Agence introuvable")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant") @PathVariable Long id) {
        agenceService.deleteById(id);
    }

    @Operation(summary = "Mettre à jour une agence en ajoutant une liste de nouveaux employés (structure imbriquée)")
    @ApiResponse(responseCode = "200", description = "Agence et employés mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Agence introuvable")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @PutMapping(value = {"/{id}/employes", "/{id}/avec-employes"})
    public Agence updateAvecEmployes(
            @Parameter(description = "Identifiant de l'agence") @PathVariable Long id,
            @RequestBody Agence agence) {
        return agenceService.updateAvecEmployes(id, agence);
    }

    @Operation(summary = "Simuler et générer un rapport sur les tarifs des véhicules d'une agence")
    @GetMapping(value = {"/{id}/simuler", "/{id}/simulation"})
    public String simuler(
            @Parameter(description = "Identifiant de l'agence") @PathVariable Long id,
            @Parameter(description = "Nombre de jours") @RequestParam(name = "jours", required = false) Integer jours,
            @Parameter(description = "Nombre de jours (alias)") @RequestParam(name = "nbJours", required = false) Integer nbJours,
            @Parameter(description = "Type d'offre (STANDARD, WEEKEND, LONGUE_DUREE)") @RequestParam VehiculeOffre offre) {
        int duree = 1;
        if (jours != null) {
            duree = jours;
        } else if (nbJours != null) {
            duree = nbJours;
        }
        return simulationService.simuler(id, duree, offre);
    }
}
