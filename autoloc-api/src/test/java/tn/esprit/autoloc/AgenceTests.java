package tn.esprit.autoloc;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IAgenceRepository;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Ignore("Legacy workshop integration tests; replaced by isolated service unit tests")
public class AgenceTests {

    @Autowired
    private AgenceRepositoryMock basicAgenceRepository;
    @Autowired
    private IAgenceRepository fullAgenceRepository;
    @Autowired
    private tn.esprit.autoloc.service.IAgenceService agenceService;

    private void addAgence(CrudRepository<Agence, Long> repository) {
        Agence agence = Agence.builder()
                .nom("Agence ariana")
                .adresse("1 Rue Hedi")
                .telephone("71585874")
                .ville("Tunis")
                .build();
        long suffix = System.currentTimeMillis() % 100000L;

        // Véhicule 1 (longueur <= 20)
        Vehicule v1 = Vehicule.builder()
                .immatriculation("96TU" + suffix)
                .marque("Isuzu")
                .modele("DMax")
                .categorie(CategorieVehicule.SUV)
                .statut(StatutVehicule.MAINTENANCE)
                .tarifJournalier(BigDecimal.valueOf(100))
                .agence(agence)
                .build();

        // Véhicule 2 (longueur <= 20)
        Vehicule v2 = Vehicule.builder()
                .immatriculation("95TU" + suffix)
                .marque("Toyota")
                .modele("Yaris")
                .categorie(CategorieVehicule.UTILITAIRE)
                .statut(StatutVehicule.DISPONIBLE)
                .tarifJournalier(BigDecimal.valueOf(80))
                .agence(agence)
                .build();

        Set<Vehicule> vehicules = new HashSet<>();
        vehicules.add(v1);
        vehicules.add(v2);
        agence.setVehicules(vehicules);

        // Persistance en cascade
        Agence saved = repository.save(agence);
        Assert.assertNotNull(saved);
        Assert.assertNotNull(saved.getIdAgence());
    }

    @Test
    public void basicAddAgence() {
        addAgence(basicAgenceRepository);
        Assert.assertTrue(basicAgenceRepository.count() > 0);
    }

    @Test
    public void fullAddAgence() {
        addAgence(fullAgenceRepository);
        Assert.assertTrue(fullAgenceRepository.count() > 0);
    }

    private void loadAgence(CrudRepository<Agence, Long> repository, String repoType) {
        Iterable<Agence> agences = repository.findAll();
        Assert.assertNotNull(agences);
        for (Agence agence : agences) {
            Assert.assertNotNull(agence.getIdAgence());
            Assert.assertNotNull(agence.getNom());
        }
    }

    @Test
    public void basicLoadAgence() {
        loadAgence(basicAgenceRepository, "basicAgenceRepository (CrudRepository)");
    }

    @Test
    public void fullLoadAgence() {
        loadAgence(fullAgenceRepository, "fullAgenceRepository (IAgenceRepository)");
    }

    @Test
    public void loadSortedAgences() {
        Iterable<Agence> agences = fullAgenceRepository.findAll(Sort.by("idAgence").descending());
        Assert.assertNotNull(agences);
    }

    @Test
    public void loadPagedAgences() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("idAgence").descending());
        Page<Agence> page = fullAgenceRepository.findAll(pageable);
        Assert.assertNotNull(page);
        Assert.assertFalse(page.getContent().isEmpty());
    }

    @Test
    public void createAgence() {
        Agence agence = Agence.builder()
                .nom("Agence Manar")
                .adresse("15 Avenue Habib Bourguiba")
                .telephone("71234567")
                .ville("Tunis")
                .build();
        Agence created = agenceService.create(agence);
        Assert.assertNotNull(created);
        Assert.assertNotNull(created.getIdAgence());
    }
}

@org.springframework.stereotype.Repository
interface AgenceRepositoryMock extends CrudRepository<Agence, Long> {
}
