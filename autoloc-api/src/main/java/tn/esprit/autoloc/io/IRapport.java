package tn.esprit.autoloc.io;

import tn.esprit.autoloc.domain.Agence;

public interface IRapport {
    String generer(Agence agence, double total);
}
