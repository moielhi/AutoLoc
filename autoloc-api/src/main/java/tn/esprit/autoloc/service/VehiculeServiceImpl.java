package tn.esprit.autoloc.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.calcul.IOffreStrategy;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
public class VehiculeServiceImpl extends GenericServiceImpl<Vehicule, Long> {

    private final Map<VehiculeOffre, IOffreStrategy> strategies;
    private final IAgenceRepository agenceRepository;
    private final IEquipementRepository equipementRepository;

    public VehiculeServiceImpl(
            IVehiculeRepository repository,
            List<IOffreStrategy> strategyList,
            IAgenceRepository agenceRepository,
            IEquipementRepository equipementRepository) {
        super(repository);
        this.strategies = new EnumMap<>(VehiculeOffre.class);
        if (strategyList != null) {
            for (IOffreStrategy s : strategyList) {
                this.strategies.put(s.getOffre(), s);
            }
        }
        this.agenceRepository = agenceRepository;
        this.equipementRepository = equipementRepository;
    }

    @Override
    protected String getEntityName() {
        return "Vehicule";
    }

    @Override
    protected Long getId(Vehicule entity) {
        return entity.getIdVehicule();
    }

    @Override
    protected void setFields(Vehicule existing, Vehicule entity) {
        existing.setImmatriculation(entity.getImmatriculation());
        existing.setMarque(entity.getMarque());
        existing.setModele(entity.getModele());
        existing.setCategorie(entity.getCategorie());
        existing.setTarifJournalier(entity.getTarifJournalier());
        existing.setStatut(entity.getStatut());
        existing.setAgence(entity.getAgence());
        existing.setReservations(entity.getReservations());
        existing.setEquipements(entity.getEquipements());
    }

    @Override
    public boolean check(Vehicule entity, boolean isNew) {
        return entity != null
                && Utils.isNotEmpty(entity.getImmatriculation())
                && Utils.isNotEmpty(entity.getMarque())
                && Utils.isNotEmpty(entity.getModele())
                && entity.getTarifJournalier() != null;
    }

    public double calculTarif(Vehicule vehicule, int nbJours, VehiculeOffre offre) {
        if (vehicule == null || vehicule.getTarifJournalier() == null) {
            return 0.0;
        }

        VehiculeOffre targetOffre = (offre != null) ? offre : VehiculeOffre.STANDARD;
        IOffreStrategy strategy = strategies.get(targetOffre);
        if (strategy != null) {
            return strategy.calculTarif(vehicule, nbJours);
        }

        return vehicule.getTarifJournalier().doubleValue() * nbJours;
    }

    public double calculTarif(Long idVehicule, int nbJours, VehiculeOffre offre) {
        Vehicule vehicule = findById(idVehicule);
        return calculTarif(vehicule, nbJours, offre);
    }

    @Transactional
    public void desaffecterAgence(Long idVehicule) {
        findById(idVehicule).setAgence(null);
    }

    @Transactional
    public void desaffecterVehicule(Long idVehicule) {
        findById(idVehicule).setAgence(null);
    }

    @Transactional
    public void affecterAgence(Long idVehicule, Long idAgence) {
        Vehicule vehicule = findById(idVehicule);
        Agence agence = agenceRepository.findById(idAgence)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", idAgence.toString()));
        vehicule.setAgence(agence);
    }

    @Transactional
    public void affecterEquipement(Long idVehicule, Long idEquipement) {
        Vehicule vehicule = findById(idVehicule);
        Equipement equipement = equipementRepository.findById(idEquipement)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", idEquipement.toString()));
        if (vehicule.getEquipements() == null) {
            vehicule.setEquipements(new HashSet<>());
        }
        vehicule.getEquipements().add(equipement);
    }
}



