package tn.esprit.autoloc.service;

import tn.esprit.autoloc.calcul.VehiculeOffre;

public interface ISimulationService {
    String simuler(Long idAgence, int nbJours, VehiculeOffre offre);
}
