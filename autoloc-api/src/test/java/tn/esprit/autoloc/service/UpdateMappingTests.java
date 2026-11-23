package tn.esprit.autoloc.service;

import org.junit.jupiter.api.Test;
import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateMappingTests {

    @Test
    void clientUpdateCopiesMutableFieldsAndReservations() {
        IClientRepository repository = mock(IClientRepository.class);
        ClientServiceImpl service = new ClientServiceImpl(repository);
        Client existing = validClient();
        existing.setIdClient(1L);
        Client update = validClient();
        update.setNom("Leila");
        update.setTelephone("71112233");
        update.setDateInscription(LocalDate.of(2026, 1, 2));
        Set<Reservation> reservations = Set.of(new Reservation());
        update.setReservations(reservations);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        service.update(1L, update);

        assertEquals("Leila", existing.getNom());
        assertEquals("71112233", existing.getTelephone());
        assertEquals(update.getDateInscription(), existing.getDateInscription());
        assertSame(reservations, existing.getReservations());
    }

    @Test
    void agenceUpdateCopiesAllFieldsAndRelationships() {
        IAgenceRepository repository = mock(IAgenceRepository.class);
        AgenceServiceImpl service = agenceService(repository);
        Agence existing = validAgence();
        existing.setIdAgence(2L);
        Agence update = validAgence();
        update.setNom("Agence Centre");
        update.setAdresse("2 Rue de Tunis");
        update.setVille("Sousse");
        update.setEffectif(3);
        Set<Vehicule> vehicles = Set.of(validVehicule());
        Set<Employe> employees = Set.of(validEmploye());
        update.setVehicules(vehicles);
        update.setEmployes(employees);
        when(repository.findById(2L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        service.update(2L, update);

        assertEquals("Agence Centre", existing.getNom());
        assertEquals("2 Rue de Tunis", existing.getAdresse());
        assertEquals("Sousse", existing.getVille());
        assertEquals(3, existing.getEffectif());
        assertSame(vehicles, existing.getVehicules());
        assertSame(employees, existing.getEmployes());
    }

    @Test
    void contratUpdateCopiesReservationAndPaymentRelationships() {
        IContratRepository repository = mock(IContratRepository.class);
        ContratServiceImpl service = new ContratServiceImpl(repository);
        Contrat existing = validContrat();
        existing.setIdContrat(3L);
        Contrat update = validContrat();
        Reservation reservation = new Reservation();
        Paiement paiement = new Paiement();
        update.setReservation(reservation);
        Set<Paiement> payments = Set.of(paiement);
        update.setPaiements(payments);
        when(repository.findById(3L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        service.update(3L, update);

        assertSame(reservation, existing.getReservation());
        assertSame(payments, existing.getPaiements());
    }

    @Test
    void employeUpdateCopiesAgenceRelationship() {
        IEmployeRepository repository = mock(IEmployeRepository.class);
        EmployeServiceImpl service = new EmployeServiceImpl(repository);
        Employe existing = validEmploye();
        existing.setIdEmploye(4L);
        Employe update = validEmploye();
        Agence agence = validAgence();
        update.setAgence(agence);
        when(repository.findById(4L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        service.update(4L, update);

        assertSame(agence, existing.getAgence());
    }

    @Test
    void equipementUpdateCopiesVehicleRelationship() {
        IEquipementRepository repository = mock(IEquipementRepository.class);
        EquipementServiceImpl service = new EquipementServiceImpl(repository);
        Equipement existing = new Equipement();
        existing.setIdEquipement(5L);
        existing.setLibelle("GPS");
        Equipement update = new Equipement();
        update.setLibelle("Dash camera");
        Set<Vehicule> vehicles = Set.of(validVehicule());
        update.setVehicules(vehicles);
        when(repository.findById(5L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        service.update(5L, update);

        assertEquals("Dash camera", existing.getLibelle());
        assertSame(vehicles, existing.getVehicules());
    }

    @Test
    void maintenanceUpdateCopiesVehicleRelationship() {
        IMaintenanceRepository repository = mock(IMaintenanceRepository.class);
        MaintenanceServiceImpl service = new MaintenanceServiceImpl(repository);
        Maintenance existing = new Maintenance();
        existing.setIdMaintenance(6L);
        existing.setDateDebut(LocalDate.of(2025, 1, 1));
        Maintenance update = new Maintenance();
        update.setDateDebut(LocalDate.of(2026, 1, 1));
        Vehicule vehicle = validVehicule();
        update.setVehicule(vehicle);
        when(repository.findById(6L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        service.update(6L, update);

        assertSame(vehicle, existing.getVehicule());
        assertEquals(update.getDateDebut(), existing.getDateDebut());
    }

    @Test
    void reservationUpdateCopiesAllRelationshipsAndStatus() {
        IReservationRepository repository = mock(IReservationRepository.class);
        ReservationServiceImpl service = new ReservationServiceImpl(repository);
        Reservation existing = validReservation();
        existing.setIdReservation(7L);
        Reservation update = validReservation();
        Client client = validClient();
        Vehicule vehicle = validVehicule();
        Contrat contrat = validContrat();
        update.setClient(client);
        update.setVehicule(vehicle);
        update.setContrat(contrat);
        update.setStatut(StatutReservation.CONFIRMEE);
        when(repository.findById(7L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        service.update(7L, update);

        assertSame(client, existing.getClient());
        assertSame(vehicle, existing.getVehicule());
        assertSame(contrat, existing.getContrat());
        assertEquals(StatutReservation.CONFIRMEE, existing.getStatut());
    }

    @Test
    void vehicleUpdateCopiesAgenceAndAssociationSets() {
        IVehiculeRepository repository = mock(IVehiculeRepository.class);
        VehiculeServiceImpl service = new VehiculeServiceImpl(
                repository, List.of(), mock(IAgenceRepository.class), mock(IEquipementRepository.class));
        Vehicule existing = validVehicule();
        existing.setIdVehicule(8L);
        Vehicule update = validVehicule();
        Agence agence = validAgence();
        update.setAgence(agence);
        Set<Reservation> reservations = Set.of(validReservation());
        Set<Equipement> equipment = Set.of(new Equipement());
        update.setReservations(reservations);
        update.setEquipements(equipment);
        when(repository.findById(8L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        service.update(8L, update);

        assertSame(agence, existing.getAgence());
        assertSame(reservations, existing.getReservations());
        assertSame(equipment, existing.getEquipements());
    }

    private static AgenceServiceImpl agenceService(IAgenceRepository repository) {
        return new AgenceServiceImpl(repository, mock(VehiculeServiceImpl.class),
                mock(EmployeServiceImpl.class), mock(IAgenceService.class));
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
        agence.setAdresse("1 Rue Hedi");
        agence.setVille("Tunis");
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
        return Vehicule.builder().immatriculation("123TU4567").marque("Toyota").modele("Yaris")
                .categorie(CategorieVehicule.CITADINE).tarifJournalier(BigDecimal.valueOf(100))
                .statut(StatutVehicule.DISPONIBLE).build();
    }

    private static Contrat validContrat() {
        Contrat contrat = new Contrat();
        contrat.setDateSignature(LocalDate.of(2026, 1, 1));
        contrat.setMontantTotal(BigDecimal.TEN);
        return contrat;
    }

    private static Reservation validReservation() {
        Reservation reservation = new Reservation();
        reservation.setDateDebut(LocalDate.of(2026, 2, 1));
        reservation.setDateFin(LocalDate.of(2026, 2, 2));
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        return reservation;
    }
}
