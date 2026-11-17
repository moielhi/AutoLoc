package tn.esprit.autoloc.calcul;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Vehicule;

@Component("weekend")
public class WeekendOffreStrategy implements IOffreStrategy {

    @Override
    public VehiculeOffre getOffre() {
        return VehiculeOffre.WEEKEND;
    }

    @Override
    public double calculTarif(Vehicule vehicule, int nbJours) {
        if (vehicule == null || vehicule.getTarifJournalier() == null) {
            return 0.0;
        }
        double tarifJournalier = vehicule.getTarifJournalier().doubleValue() * 0.80; // réduction de 20%
        return tarifJournalier * nbJours;
    }
}
