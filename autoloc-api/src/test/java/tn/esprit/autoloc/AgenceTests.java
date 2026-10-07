package tn.esprit.autoloc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AgenceTests {

    @Autowired
    private AgenceRepositoryMock agenceRepository;

    @Test
    public void addAgence() {
        // Agence
        Agence agence = Agence.builder()
                .nom("Agence ariana")
                .adresse("1 Rue Hedi")
                .telephone("71585874")
                .ville("Tunis")
                .build();

        // Véhicule 1
        Vehicule v1 = Vehicule.builder()
                .immatriculation("785414TU96")
                .marque("Isuzu")
                .modele("DMax")
                .categorie(CategorieVehicule.SUV)
                .statut(StatutVehicule.MAINTENANCE)
                .tarifJournalier(BigDecimal.valueOf(100))
                .agence(agence)
                .build();

        // Véhicule 2
        Vehicule v2 = Vehicule.builder()
                .immatriculation("785414TU95")
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
        agenceRepository.save(agence);
    }

    @Test
    public void loadAgence() {
        StringBuilder sb = new StringBuilder("\n");
        for (Agence agence : agenceRepository.findAll()) {
            sb.append(agence.getIdAgence()).append(" | ").append(agence.getNom()).append("\n");
            sb.append("Vehicules Count : ").append(agence.getVehicules().size()).append("\n");
            for (Vehicule v : agence.getVehicules()) {
                sb.append("=== ").append(v.getIdVehicule()).append("|").append(v.getImmatriculation()).append("\n");
            }
        }
        fail(sb.toString());
    }
}

interface AgenceRepositoryMock extends CrudRepository<Agence, Long> {
}