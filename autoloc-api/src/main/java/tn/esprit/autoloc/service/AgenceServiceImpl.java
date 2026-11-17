package tn.esprit.autoloc.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IAgenceRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("agenceServiceImpl")
@Primary
public class AgenceServiceImpl extends GenericServiceImpl<Agence, Long> implements IAgenceService {

    private final VehiculeServiceImpl vehiculeService;
    private final EmployeServiceImpl employeService;

    public AgenceServiceImpl(IAgenceRepository repository, VehiculeServiceImpl vehiculeService, EmployeServiceImpl employeService) {
        super(repository);
        this.vehiculeService = vehiculeService;
        this.employeService = employeService;
    }

    @Override
    protected String getEntityName() {
        return "Agence";
    }

    @Override
    protected Long getId(Agence entity) {
        return entity.getIdAgence();
    }

    @Override
    protected void setFields(Agence existing, Agence entity) {
        existing.setNom(entity.getNom());
        existing.setAdresse(entity.getAdresse());
        existing.setVille(entity.getVille());
        existing.setTelephone(entity.getTelephone());
        existing.setEffectif(entity.getEffectif());
        existing.setVehicules(entity.getVehicules());
        existing.setEmployes(entity.getEmployes());
    }

    @Override
    public boolean check(Agence agence, boolean isNew) {
        return agence != null && Utils.isNotEmpty(agence.getNom()) && Utils.isNotEmpty(agence.getTelephone());
    }

    @Override
    public boolean hasVehicules(Long id) {
        Agence agence = findById(id);
        return agence.getVehicules() != null && !agence.getVehicules().isEmpty();
    }

    @Override
    @Transactional
    public Agence updateAvecEmployes(Long idAgence, Agence agence, List<Employe> employes) {
        return doUpdateAvecEmployes(idAgence, agence, employes);
    }

    @Override
    @Transactional
    public Agence updateAvecEmployes(Long idAgence, Agence agence) {
        List<Employe> employesList = agence.getEmployes() != null
                ? new ArrayList<>(agence.getEmployes())
                : Collections.emptyList();
        return doUpdateAvecEmployes(idAgence, agence, employesList);
    }

    private Agence doUpdateAvecEmployes(Long idAgence, Agence agence, List<Employe> employes) {
        // 1. Mettre à jour d'abord l'agence
        Agence updatedAgence = this.update(idAgence, agence);

        // 2. Utiliser le service des employés pour insérer les employés
        if (employes != null && !employes.isEmpty()) {
            for (Employe emp : employes) {
                updatedAgence.addEmploye(emp);
                employeService.create(emp);
            }
            // 3. Mettre à jour le champ créé précédemment en utilisant la taille de la liste des employés
            updatedAgence.setEffectif(employes.size());
            updatedAgence = repository.save(updatedAgence);
        }
        return updatedAgence;
    }

    public String simuler(Long idAgence, int nbJours, VehiculeOffre offre) {
        Agence agence = findById(idAgence);
        return simuler(agence, nbJours, offre);
    }

    public String simuler(Agence agence, int nbJours, VehiculeOffre offre) {
        StringBuilder rapport = new StringBuilder();

        rapport.append("=== Rapport de Simulation ===").append(System.lineSeparator())
                .append("Id : ").append(agence.getIdAgence())
                .append(", Nom : ").append(agence.getNom())
                .append(", Tel : ").append(agence.getTelephone())
                .append(System.lineSeparator());

        double total = 0;
        if (agence.getVehicules() != null) {
            for (Vehicule vehicule : agence.getVehicules()) {
                total += vehiculeService.calculTarif(vehicule, nbJours, offre);
            }
        }

        rapport.append("Total tarif : ").append(total);

        return rapport.toString();
    }
}
