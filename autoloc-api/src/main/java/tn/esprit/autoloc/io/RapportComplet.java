package tn.esprit.autoloc.io;

import tn.esprit.autoloc.domain.Agence;

public class RapportComplet implements IRapport {
    @Override
    public String generer(Agence agence, double total) {
        StringBuilder rapport = new StringBuilder();
        rapport.append("=== Rapport de Simulation ===").append(System.lineSeparator())
                .append("Id : ").append(agence.getIdAgence())
                .append(", Nom : ").append(agence.getNom())
                .append(", Tel : ").append(agence.getTelephone())
                .append(System.lineSeparator())
                .append("Total tarif : ").append(total);
        return rapport.toString();
    }
}
