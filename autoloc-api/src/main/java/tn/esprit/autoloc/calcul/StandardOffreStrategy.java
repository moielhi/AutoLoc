package tn.esprit.autoloc.calcul;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Vehicule;

@Component("standard")
public class StandardOffreStrategy implements IOffreStrategy {

    @Override
    public VehiculeOffre getOffre() {
        return VehiculeOffre.STANDARD;
    }

    @Override
    public double calculTarif(Vehicule vehicule, int nbJours) {
        if (vehicule == null || vehicule.getTarifJournalier() == null) {
            return 0.0;
        }
        return vehicule.getTarifJournalier().doubleValue() * nbJours;
    }
}
