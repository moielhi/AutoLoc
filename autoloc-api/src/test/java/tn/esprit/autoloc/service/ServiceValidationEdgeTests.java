package tn.esprit.autoloc.service;

import org.junit.jupiter.api.Test;
import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

class ServiceValidationEdgeTests {

    @Test
    void clientValidationRejectsNullEntity() {
        assertFalse(new ClientServiceImpl(mock(IClientRepository.class)).check(null, true));
    }

    @Test
    void clientValidationRejectsBlankNom() {
        Client client = validClient();
        client.setNom(" \t ");
        assertFalse(new ClientServiceImpl(mock(IClientRepository.class)).check(client, true));
    }

    @Test
    void clientValidationRejectsBlankPrenom() {
        Client client = validClient();
        client.setPrenom(" ");
        assertFalse(new ClientServiceImpl(mock(IClientRepository.class)).check(client, true));
    }

    @Test
    void clientValidationRejectsBlankEmail() {
        Client client = validClient();
        client.setEmail("  ");
        assertFalse(new ClientServiceImpl(mock(IClientRepository.class)).check(client, true));
    }

    @Test
    void clientValidationRejectsBlankDriverLicense() {
        Client client = validClient();
        client.setNumPermis("\t");
        assertFalse(new ClientServiceImpl(mock(IClientRepository.class)).check(client, true));
    }

    @Test
    void agenceValidationRejectsNullEntity() {
        assertFalse(agenceService().check(null, true));
    }

    @Test
    void agenceValidationRejectsBlankName() {
        Agence agence = validAgence();
        agence.setNom("  ");
        assertFalse(agenceService().check(agence, true));
    }

    @Test
    void agenceValidationRejectsBlankTelephone() {
        Agence agence = validAgence();
        agence.setTelephone(" ");
        assertFalse(agenceService().check(agence, true));
    }

    @Test
    void contratValidationRejectsNullEntity() {
        assertFalse(new ContratServiceImpl(mock(IContratRepository.class)).check(null, true));
    }

    @Test
    void employeValidationRejectsBlankPrenom() {
        Employe employe = validEmploye();
        employe.setPrenom(" ");
        assertFalse(new EmployeServiceImpl(mock(IEmployeRepository.class)).check(employe, true));
    }

    @Test
    void equipementValidationRejectsNullEntity() {
        assertFalse(new EquipementServiceImpl(mock(IEquipementRepository.class)).check(null, true));
    }

    @Test
    void maintenanceValidationRejectsNullStartDate() {
        Maintenance maintenance = new Maintenance();
        maintenance.setDateFin(LocalDate.now());
        assertFalse(new MaintenanceServiceImpl(mock(IMaintenanceRepository.class)).check(maintenance, true));
    }

    @Test
    void reservationValidationRejectsMissingStartDate() {
        Reservation reservation = new Reservation();
        reservation.setDateFin(LocalDate.now().plusDays(1));
        assertFalse(new ReservationServiceImpl(mock(IReservationRepository.class)).check(reservation, true));
    }

    @Test
    void reservationValidationRejectsMissingEndDate() {
        Reservation reservation = new Reservation();
        reservation.setDateDebut(LocalDate.now());
        assertFalse(new ReservationServiceImpl(mock(IReservationRepository.class)).check(reservation, true));
    }

    @Test
    void vehiculeValidationRejectsBlankRegistration() {
        Vehicule vehicule = validVehicule();
        vehicule.setImmatriculation(" ");
        assertFalse(vehiculeService().check(vehicule, true));
    }

    @Test
    void vehiculeValidationRejectsMissingDailyRate() {
        Vehicule vehicule = validVehicule();
        vehicule.setTarifJournalier(null);
        assertFalse(vehiculeService().check(vehicule, true));
    }

    private static AgenceServiceImpl agenceService() {
        return new AgenceServiceImpl(
                mock(IAgenceRepository.class), mock(VehiculeServiceImpl.class),
                mock(EmployeServiceImpl.class), mock(IAgenceService.class));
    }

    private static VehiculeServiceImpl vehiculeService() {
        return new VehiculeServiceImpl(
                mock(IVehiculeRepository.class), java.util.List.of(),
                mock(IAgenceRepository.class), mock(IEquipementRepository.class));
    }

    private static Client validClient() {
        Client client = new Client();
        client.setNom("Nour");
        client.setPrenom("Ali");
        client.setEmail("nour@example.com");
        client.setNumPermis("TN123456");
        return client;
    }

    private static Agence validAgence() {
        Agence agence = new Agence();
        agence.setNom("Agence Ariana");
        agence.setTelephone("71585874");
        return agence;
    }

    private static Employe validEmploye() {
        Employe employe = new Employe();
        employe.setNom("Sami");
        employe.setPrenom("Ben Ali");
        employe.setRole(RoleEmploye.AGENT);
        return employe;
    }

    private static Vehicule validVehicule() {
        return Vehicule.builder()
                .immatriculation("123TU4567")
                .marque("Toyota")
                .modele("Yaris")
                .categorie(CategorieVehicule.CITADINE)
                .tarifJournalier(BigDecimal.valueOf(100))
                .statut(StatutVehicule.DISPONIBLE)
                .build();
    }
}
