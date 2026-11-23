package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.VehiculeServiceImpl;

import java.util.List;

@Tag(name = "Véhicules", description = "Gestion du parc de véhicules")
@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
@SuppressWarnings("java:S4684")
public class VehiculeController {

    private final VehiculeServiceImpl vehiculeService;

    @Operation(summary = "Lister tous les véhicules")
    @ApiResponse(responseCode = "200", description = "Liste des véhicules récupérée avec succès")
    @GetMapping
    public List<Vehicule> findAll() {
        return vehiculeService.findAll();
    }

    @Operation(summary = "Consulter un véhicule")
    @ApiResponse(responseCode = "200", description = "Véhicule trouvé")
    @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    @GetMapping("/{id}")
    public Vehicule findById(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        return vehiculeService.findById(id);
    }

    @Operation(summary = "Ajouter un véhicule")
    @ApiResponse(responseCode = "201", description = "Véhicule créé")
    @ApiResponse(responseCode = "400", description = "Données invalides ou identifiant fourni")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Vehicule create(@RequestBody Vehicule vehicule) {
        return vehiculeService.create(vehicule);
    }

    @Operation(summary = "Modifier un véhicule")
    @ApiResponse(responseCode = "200", description = "Véhicule modifié")
    @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @PutMapping("/{id}")
    public Vehicule update(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id, @RequestBody Vehicule vehicule) {
        return vehiculeService.update(id, vehicule);
    }

    @Operation(summary = "Supprimer un véhicule")
    @ApiResponse(responseCode = "204", description = "Véhicule supprimé")
    @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        vehiculeService.deleteById(id);
    }

    @Operation(summary = "Désaffecter un véhicule de son agence")
    @ApiResponse(responseCode = "204", description = "Véhicule désaffecté avec succès")
    @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    @PutMapping(value = {"/{id}/desaffecter", "/{id}/agence/desaffecter"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desaffecterAgence(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        vehiculeService.desaffecterAgence(id);
    }

    @Operation(summary = "Désaffecter un véhicule de son agence (DELETE alias)")
    @DeleteMapping("/{id}/agence")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desaffecterAgenceDelete(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        vehiculeService.desaffecterAgence(id);
    }

    @Operation(summary = "Affecter une agence à un véhicule choisi")
    @ApiResponse(responseCode = "204", description = "Agence affectée au véhicule")
    @ApiResponse(responseCode = "404", description = "Véhicule ou Agence introuvable")
    @PutMapping(value = {"/{idVehicule}/agence/{idAgence}", "/{idVehicule}/agences/{idAgence}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void affecterAgence(
            @Parameter(description = "Identifiant du véhicule") @PathVariable Long idVehicule,
            @Parameter(description = "Identifiant de l'agence") @PathVariable Long idAgence) {
        vehiculeService.affecterAgence(idVehicule, idAgence);
    }

    @Operation(summary = "Affecter un équipement à un véhicule choisi")
    @ApiResponse(responseCode = "204", description = "Équipement affecté au véhicule")
    @ApiResponse(responseCode = "404", description = "Véhicule ou Équipement introuvable")
    @PutMapping(value = {"/{idVehicule}/equipement/{idEquipement}", "/{idVehicule}/equipements/{idEquipement}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void affecterEquipement(
            @Parameter(description = "Identifiant du véhicule") @PathVariable Long idVehicule,
            @Parameter(description = "Identifiant de l'équipement") @PathVariable Long idEquipement) {
        vehiculeService.affecterEquipement(idVehicule, idEquipement);
    }
}
