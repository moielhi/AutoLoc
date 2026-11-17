package tn.esprit.autoloc.calcul;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Vehicule;

@Component("longueDuree")
public class LongueDureeOffreStrategy implements IOffreStrategy {

    @Override
    public VehiculeOffre getOffre() {
        return VehiculeOffre.LONGUE_DUREE;
    }

    @Override
    public double calculTarif(Vehicule vehicule, int nbJours) {
        if (vehicule == null || vehicule.getTarifJournalier() == null) {
            return 0.0;
        }
        double tarifJournalier = vehicule.getTarifJournalier().doubleValue() * 0.75; // réduction de 25%
        double total = tarifJournalier * nbJours;
        if (total > 2000) {
            total *= 0.95; // réduction de 5% supplémentaire si > 2000
        }
        return total;
    }
}
