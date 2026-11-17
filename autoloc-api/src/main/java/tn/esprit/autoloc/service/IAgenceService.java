package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;

import java.util.List;

public interface IAgenceService extends IGenericService<Agence, Long> {
    boolean hasVehicules(Long id);
    Agence updateAvecEmployes(Long idAgence, Agence agence);
    Agence updateAvecEmployes(Long idAgence, Agence agence, List<Employe> employes);
}
