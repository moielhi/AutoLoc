package tn.esprit.autoloc.calcul;

import org.junit.jupiter.api.Test;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.io.RapportComplet;
import tn.esprit.autoloc.io.RapportConsole;
import tn.esprit.autoloc.io.RapportFactory;
import tn.esprit.autoloc.web.ErrorResponse;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PricingAndReportTests {

    @Test
    void utilsRejectsNull() {
        assertFalse(Utils.isNotEmpty(null));
    }

    @Test
    void utilsRejectsEmptyString() {
        assertFalse(Utils.isNotEmpty(""));
    }

    @Test
    void utilsRejectsWhitespaceOnlyString() {
        assertFalse(Utils.isNotEmpty(" \t\n"));
    }

    @Test
    void utilsAcceptsNonBlankString() {
        assertTrue(Utils.isNotEmpty(" AutoLoc "));
    }

    @Test
    void standardOfferMultipliesDailyRateByDays() {
        assertEquals(300.0, new StandardOffreStrategy().calculTarif(vehicule(100), 3), 0.001);
    }

    @Test
    void standardOfferReturnsZeroForNullVehicle() {
        assertEquals(0.0, new StandardOffreStrategy().calculTarif(null, 3), 0.001);
    }

    @Test
    void standardOfferReturnsZeroForMissingDailyRate() {
        Vehicule vehicule = vehicule(100);
        vehicule.setTarifJournalier(null);

        assertEquals(0.0, new StandardOffreStrategy().calculTarif(vehicule, 3), 0.001);
    }

    @Test
    void weekendOfferAppliesTwentyPercentDiscount() {
        assertEquals(240.0, new WeekendOffreStrategy().calculTarif(vehicule(100), 3), 0.001);
    }

    @Test
    void weekendOfferReturnsZeroForNullVehicle() {
        assertEquals(0.0, new WeekendOffreStrategy().calculTarif(null, 3), 0.001);
    }

    @Test
    void weekendOfferReturnsZeroForMissingDailyRate() {
        Vehicule vehicule = vehicule(100);
        vehicule.setTarifJournalier(null);

        assertEquals(0.0, new WeekendOffreStrategy().calculTarif(vehicule, 3), 0.001);
    }

    @Test
    void longDurationOfferAppliesTwentyFivePercentDiscountBelowThreshold() {
        assertEquals(1950.0, new LongueDureeOffreStrategy().calculTarif(vehicule(100), 26), 0.001);
    }

    @Test
    void longDurationOfferAddsFivePercentDiscountAboveThreshold() {
        assertEquals(1923.75, new LongueDureeOffreStrategy().calculTarif(vehicule(100), 27), 0.001);
    }

    @Test
    void longDurationOfferReturnsZeroForNullVehicle() {
        assertEquals(0.0, new LongueDureeOffreStrategy().calculTarif(null, 10), 0.001);
    }

    @Test
    void reportFactorySelectsConsoleCaseInsensitively() {
        assertInstanceOf(RapportConsole.class, RapportFactory.createRapport("console"));
    }

    @Test
    void reportFactoryUsesCompleteReportForOtherTypes() {
        assertInstanceOf(RapportComplet.class, RapportFactory.createRapport("PDF"));
    }

    @Test
    void consoleReportReturnsTheTotalAsText() {
        assertEquals("125.5", new RapportConsole().generer(agence(), 125.5));
    }

    @Test
    void completeReportIncludesAgencyDetailsAndTotal() {
        String report = new RapportComplet().generer(agence(), 125.5);

        assertTrue(report.contains("Id : 9, Nom : Tunis Centre, Tel : 71000000"));
        assertTrue(report.contains("Total tarif : 125.5"));
    }

    @Test
    void errorResponseConstructorSetsTimestampAndMessage() {
        ErrorResponse response = new ErrorResponse(404, "Missing");

        assertEquals(404, response.getStatut());
        assertEquals("Missing", response.getMessage());
        assertNotNull(response.getHorodatage());
    }

    private static Vehicule vehicule(double dailyRate) {
        return Vehicule.builder()
                .immatriculation("123TU4567")
                .marque("Toyota")
                .modele("Yaris")
                .categorie(CategorieVehicule.CITADINE)
                .tarifJournalier(BigDecimal.valueOf(dailyRate))
                .statut(StatutVehicule.DISPONIBLE)
                .build();
    }

    private static Agence agence() {
        Agence agence = new Agence();
        agence.setIdAgence(9L);
        agence.setNom("Tunis Centre");
        agence.setTelephone("71000000");
        return agence;
    }
}
