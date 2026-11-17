package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BasicAgenceServiceImpl implements IAgenceService {
    private final IAgenceRepository repository;

    @Override
    public boolean check(Agence agence, boolean isNew) {
        return agence != null && Utils.isNotEmpty(agence.getNom()) && Utils.isNotEmpty(agence.getTelephone());
    }

    @Override
    public Agence create(Agence agence) {
        if (agence.getIdAgence() != null) {
            throw new IllegalArgumentException("Identifiant non null !!!!!");
        }
        if (!check(agence, true)) {
            throw new IllegalArgumentException("Certains champs sont invalides !");
        }
        return repository.save(agence);
    }

    @Override
    public Agence findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Agence", id.toString()));
    }

    @Override
    public List<Agence> findAll() {
        return repository.findAll();
    }

    @Override
    public Agence update(Long id, Agence agence) {
        Agence existing = findById(id);
        if (!check(agence, false)) {
            throw new IllegalArgumentException("Certains champs sont invalides !");
        }
        existing.setNom(agence.getNom());
        existing.setAdresse(agence.getAdresse());
        existing.setVille(agence.getVille());
        existing.setTelephone(agence.getTelephone());
        existing.setEffectif(agence.getEffectif());
        existing.setVehicules(agence.getVehicules());
        existing.setEmployes(agence.getEmployes());
        return repository.save(existing);
    }

    @Override
    public Agence updateAvecEmployes(Long idAgence, Agence agence) {
        throw new UnsupportedOperationException("Non implemente dans BasicAgenceServiceImpl");
    }

    @Override
    public Agence updateAvecEmployes(Long idAgence, Agence agence, java.util.List<tn.esprit.autoloc.domain.Employe> employes) {
        throw new UnsupportedOperationException("Non implemente dans BasicAgenceServiceImpl");
    }

    @Override
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Agence", id.toString());
        }
        repository.deleteById(id);
    }

    @Override
    public boolean hasVehicules(Long id) {
        Agence agence = findById(id);
        return agence.getVehicules() != null && !agence.getVehicules().isEmpty();
    }
}
