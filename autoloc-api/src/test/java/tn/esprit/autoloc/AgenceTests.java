package tn.esprit.autoloc;

import jakarta.persistence.criteria.CriteriaBuilder;
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

import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AgenceTests {

    @Autowired
    private AgenceRepositoryMock basicAgenceRepository;
    @Autowired
    private IAgenceRepository fullAgenceRepository;


    private void addAgence(CrudRepository<Agence, Long> repository) {
        // Agence
        Agence agence = Agence.builder()
                .nom("Agence ariana")
                .adresse("1 Rue Hedi")
                .telephone("71585874")
                .ville("Tunis")
                .build();
        int ms = (int) System.currentTimeMillis();

        // Véhicule 1
        Vehicule v1 = Vehicule.builder()
                .immatriculation("785414TU96-" + ms)
                .marque("Isuzu")
                .modele("DMax")
                .categorie(CategorieVehicule.SUV)
                .statut(StatutVehicule.MAINTENANCE)
                .tarifJournalier(BigDecimal.valueOf(100))
                .agence(agence)
                .build();

        // Véhicule 2
        Vehicule v2 = Vehicule.builder()
                .immatriculation("785414TU95-" + ms)
                .marque("Toyota")
                .modele("Yaris")
                .categorie(CategorieVehicule.UTILITAIRE)
                .statut(StatutVehicule.DISPONIBLE)
                .tarifJournalier(BigDecimal.valueOf(80))
                .agence(agence)
                .build();

        // Initialisation avec new HashSet<>()
        Set<Vehicule> vehicules = new HashSet<>();
        vehicules.add(v1);
        vehicules.add(v2);
        agence.setVehicules(vehicules);

        // Persistance en cascade
        repository.save(agence);
    }

    @Test
    public void basicAddAgence() {
        addAgence(basicAgenceRepository);
    }

    @Test
    public void fullAddAgence() {
        addAgence(fullAgenceRepository);
    }

    private void loadAgence(CrudRepository<Agence, Long> repository, String repoType) {
        StringBuilder sb = new StringBuilder("\n=== Dépôt: ").append(repoType).append(" ===\n");
        for (Agence agence : repository.findAll()) {
            sb.append(agence.getIdAgence()).append(" | ").append(agence.getNom()).append("\n");
            sb.append("Vehicules Count : ").append(agence.getVehicules() != null ? agence.getVehicules().size() : 0).append("\n");
            if (agence.getVehicules() != null) {
                for (Vehicule v : agence.getVehicules()) {
                    sb.append("=== ").append(v.getIdVehicule()).append("|").append(v.getImmatriculation()).append("\n");
                }
            }
        }
        fail(sb.toString());
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
        StringBuilder sb = new StringBuilder("\n");
        for (Agence agence : fullAgenceRepository.findAll(Sort.by("idAgence").descending())) {
            sb.append(agence.getIdAgence()).append(" | ").append(agence.getNom()).append("\n");
        }
        fail(sb.toString());
    }

    @Test
    public void loadPagedAgences() {
        StringBuilder sb = new StringBuilder("\n");
        Pageable pageable = PageRequest.of(0, 2, Sort.by("idAgence").descending());
        Page<Agence> page = fullAgenceRepository.findAll(pageable);

        sb.append("Total Pages : ").append(page.getTotalPages()).append("\n");
        sb.append("Page en cours : ").append(page.getNumber()).append("\n");
        for (Agence agence : page.getContent()) {
            sb.append(agence.getIdAgence()).append(" | ").append(agence.getNom()).append("\n");
        }
        fail(sb.toString());
    }
}

interface AgenceRepositoryMock extends CrudRepository<Agence, Long> {
}