package tn.esprit.autoloc.calcul;

import tn.esprit.autoloc.domain.Vehicule;

public interface IOffreStrategy {
    VehiculeOffre getOffre();
    double calculTarif(Vehicule vehicule, int nbJours);
}
