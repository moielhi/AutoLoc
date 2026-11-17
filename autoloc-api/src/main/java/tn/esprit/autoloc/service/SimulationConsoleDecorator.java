package tn.esprit.autoloc.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.io.IRapport;
import tn.esprit.autoloc.io.RapportFactory;

@Service
@Primary
public class SimulationConsoleDecorator implements ISimulationService {

    private final IAgenceService agenceService;
    private final VehiculeServiceImpl vehiculeService;

    public SimulationConsoleDecorator(
            @Qualifier("agenceServiceImpl") IAgenceService agenceService,
            VehiculeServiceImpl vehiculeService) {
        this.agenceService = agenceService;
        this.vehiculeService = vehiculeService;
    }

    @Override
    public String simuler(Long idAgence, int nbJours, VehiculeOffre offre) {
        Agence agence = agenceService.findById(idAgence);
        double total = 0.0;
        if (agence.getVehicules() != null) {
            for (Vehicule vehicule : agence.getVehicules()) {
                total += vehiculeService.calculTarif(vehicule, nbJours, offre);
            }
        }
        IRapport rapport = RapportFactory.createRapport("CONSOLE");
        return rapport.generer(agence, total);
    }
}
