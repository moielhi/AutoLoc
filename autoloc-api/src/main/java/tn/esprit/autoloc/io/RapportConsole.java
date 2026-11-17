package tn.esprit.autoloc.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tn.esprit.autoloc.domain.Agence;

public class RapportConsole implements IRapport {

    private static final Logger log = LoggerFactory.getLogger(RapportConsole.class);

    @Override
    public String generer(Agence agence, double total) {
        log.info("=== En-tête Agence (Console) ===");
        if (log.isInfoEnabled()) {
            log.info("Id : {}, Nom : {}, Tel : {}", agence.getIdAgence(), agence.getNom(), agence.getTelephone());
        }
        return String.valueOf(total);
    }
}
