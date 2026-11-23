package tn.esprit.autoloc.service;

import org.junit.jupiter.api.Test;
import tn.esprit.autoloc.calcul.IOffreStrategy;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServiceUnitTests {

    @Test
    void genericCrudOperationsAreValidatedAndDelegatedToTheRepository() {
        IClientRepository repository = mock(IClientRepository.class);
        ClientServiceImpl service = new ClientServiceImpl(repository);
        Client client = validClient();
        when(repository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertSame(client, service.create(client));

        Client existing = validClient();
        existing.setIdClient(7L);
        when(repository.findById(7L)).thenReturn(Optional.of(existing));
        Client update = validClient();
        update.setNom("Updated");
        assertSame(existing, service.update(7L, update));
        assertEquals("Updated", existing.getNom());

        assertSame(existing, service.findById(7L));
        when(repository.existsById(7L)).thenReturn(true);
        service.deleteById(7L);
        verify(repository).deleteById(7L);
    }

    @Test
    void genericCrudOperationsReportMissingEntitiesAndRejectInvalidInput() {
        IClientRepository repository = mock(IClientRepository.class);
        ClientServiceImpl service = new ClientServiceImpl(repository);

        assertThrows(IllegalArgumentException.class, () -> service.create(new Client()));
        Client withId = validClient();
        withId.setIdClient(9L);
        assertThrows(IllegalArgumentException.class, () -> service.create(withId));
        assertThrows(ResourceNotFoundException.class, () -> service.findById(9L));
        verify(repository, never()).save(any(Client.class));
    }

    @Test
    void genericCrudOperationsRejectInvalidUpdatesAndMissingDeletes() {
        IClientRepository repository = mock(IClientRepository.class);
        ClientServiceImpl service = new ClientServiceImpl(repository);
        when(repository.findById(8L)).thenReturn(Optional.of(validClient()));
        when(repository.existsById(9L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.update(8L, new Client()));
        assertThrows(ResourceNotFoundException.class, () -> service.deleteById(9L));
        verify(repository, never()).deleteById(9L);
    }

    @Test
    void agenceServicesValidateAndCheckVehiclePresence() {
        IAgenceRepository repository = mock(IAgenceRepository.class);
        IAgenceService proxy = mock(IAgenceService.class);
        AgenceServiceImpl service = new AgenceServiceImpl(
                repository, mock(VehiculeServiceImpl.class), mock(EmployeServiceImpl.class), proxy);
        Agence agence = validAgence();
        agence.setVehicules(Set.of(validVehicule()));
        when(repository.findById(1L)).thenReturn(Optional.of(agence));

        assertTrue(service.check(agence, true));
        assertFalse(service.check(new Agence(), true));
        assertTrue(service.hasVehicules(1L));
    }

    @Test
    void agenceUpdateUsesTheInjectedServiceAndAddsEmployees() {
        IAgenceRepository repository = mock(IAgenceRepository.class);
        IAgenceService proxy = mock(IAgenceService.class);
        EmployeServiceImpl employeService = mock(EmployeServiceImpl.class);
        AgenceServiceImpl service = new AgenceServiceImpl(
                repository, mock(VehiculeServiceImpl.class), employeService, proxy);
        Agence agence = validAgence();
        Employe employe = validEmploye();
        when(proxy.update(1L, agence)).thenReturn(agence);
        when(repository.save(agence)).thenReturn(agence);
        when(employeService.create(employe)).thenReturn(employe);

        Agence updated = service.updateAvecEmployes(1L, agence, List.of(employe));

        assertSame(agence, updated);
        assertEquals(1, updated.getEffectif());
        assertTrue(updated.getEmployes().contains(employe));
        verify(proxy).update(1L, agence);
        verify(employeService).create(employe);
    }

    @Test
    void agenceUpdateWithoutEmployeesDoesNotWriteEmployeeRows() {
        IAgenceRepository repository = mock(IAgenceRepository.class);
        IAgenceService proxy = mock(IAgenceService.class);
        EmployeServiceImpl employeService = mock(EmployeServiceImpl.class);
        AgenceServiceImpl service = new AgenceServiceImpl(
                repository, mock(VehiculeServiceImpl.class), employeService, proxy);
        Agence agence = validAgence();
        when(proxy.update(1L, agence)).thenReturn(agence);

        assertSame(agence, service.updateAvecEmployes(1L, agence));
        verify(proxy).update(1L, agence);
        verify(repository, never()).save(any(Agence.class));
        verifyNoInteractions(employeService);
    }

    @Test
    void agenceServiceReportsVehicleAbsenceAndBuildsSimulationTotals() {
        IAgenceRepository repository = mock(IAgenceRepository.class);
        VehiculeServiceImpl vehiculeService = mock(VehiculeServiceImpl.class);
        AgenceServiceImpl service = new AgenceServiceImpl(
                repository, vehiculeService, mock(EmployeServiceImpl.class), mock(IAgenceService.class));
        Agence agence = validAgence();
        agence.setIdAgence(1L);
        agence.setVehicules(Set.of(validVehicule()));
        when(repository.findById(1L)).thenReturn(Optional.of(agence));
        Agence agenceSansVehicules = validAgence();
        when(repository.findById(2L)).thenReturn(Optional.of(agenceSansVehicules));
        when(vehiculeService.calculTarif(any(Vehicule.class), eq(2), eq(VehiculeOffre.WEEKEND)))
                .thenReturn(160.0);

        assertFalse(service.hasVehicules(2L));
        String report = service.simuler(1L, 2, VehiculeOffre.WEEKEND);

        assertTrue(report.contains("Id : 1"));
        assertTrue(report.contains("Total tarif : 160.0"));
        verify(vehiculeService).calculTarif(any(Vehicule.class), eq(2), eq(VehiculeOffre.WEEKEND));
    }

    @Test
    void basicAgenceServiceCreatesValidAgencesAndRejectsInvalidOnes() {
        IAgenceRepository repository = mock(IAgenceRepository.class);
        BasicAgenceServiceImpl service = new BasicAgenceServiceImpl(repository);
        Agence agence = validAgence();
        when(repository.save(agence)).thenReturn(agence);

        assertSame(agence, service.create(agence));
        assertThrows(IllegalArgumentException.class, () -> service.create(new Agence()));
        verify(repository).save(agence);
    }

    @Test
    void basicAgenceServiceReadsUpdatesAndDeletesAgences() {
        IAgenceRepository repository = mock(IAgenceRepository.class);
        BasicAgenceServiceImpl service = new BasicAgenceServiceImpl(repository);
        Agence existing = validAgence();
        existing.setIdAgence(4L);
        Agence update = validAgence();
        update.setNom("Agence updated");
        when(repository.findById(4L)).thenReturn(Optional.of(existing));
        when(repository.findAll()).thenReturn(List.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(repository.existsById(4L)).thenReturn(true);

        assertSame(existing, service.findById(4L));
        assertEquals(List.of(existing), service.findAll());
        assertSame(existing, service.update(4L, update));
        assertEquals("Agence updated", existing.getNom());
        service.deleteById(4L);
        verify(repository).deleteById(4L);
    }

    @Test
    void basicAgenceServiceReportsMissingEntitiesAndUnsupportedNestedUpdates() {
        IAgenceRepository repository = mock(IAgenceRepository.class);
        BasicAgenceServiceImpl service = new BasicAgenceServiceImpl(repository);

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
        assertThrows(ResourceNotFoundException.class, () -> service.deleteById(99L));
        assertThrows(UnsupportedOperationException.class,
                () -> service.updateAvecEmployes(1L, validAgence()));
        assertThrows(UnsupportedOperationException.class,
                () -> service.updateAvecEmployes(1L, validAgence(), List.of(validEmploye())));
    }

    @Test
    void clientServiceChecksRequiredFields() {
        ClientServiceImpl service = new ClientServiceImpl(mock(IClientRepository.class));

        assertTrue(service.check(validClient(), true));
        assertFalse(service.check(new Client(), true));
        Client missingName = validClient();
        missingName.setNom(" ");
        assertFalse(service.check(missingName, true));
    }

    @Test
    void contratServiceUpdatesContractFields() {
        IContratRepository repository = mock(IContratRepository.class);
        ContratServiceImpl service = new ContratServiceImpl(repository);
        Contrat existing = new Contrat();
        existing.setIdContrat(2L);
        existing.setDateSignature(LocalDate.of(2025, 1, 1));
        existing.setMontantTotal(BigDecimal.TEN);
        Contrat update = new Contrat();
        update.setDateSignature(LocalDate.of(2026, 1, 1));
        update.setMontantTotal(BigDecimal.valueOf(200));
        update.setValide(true);
        when(repository.findById(2L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        assertSame(existing, service.update(2L, update));
        assertEquals(update.getDateSignature(), existing.getDateSignature());
        assertEquals(update.getMontantTotal(), existing.getMontantTotal());
        assertTrue(existing.isValide());
    }

    @Test
    void employeServiceUpdatesEmployeeFields() {
        IEmployeRepository repository = mock(IEmployeRepository.class);
        EmployeServiceImpl service = new EmployeServiceImpl(repository);
        Employe existing = validEmploye();
        existing.setIdEmploye(3L);
        Employe update = validEmploye();
        update.setNom("Leila");
        update.setRole(RoleEmploye.MANAGER);
        when(repository.findById(3L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        assertSame(existing, service.update(3L, update));
        assertEquals("Leila", existing.getNom());
        assertEquals(RoleEmploye.MANAGER, existing.getRole());
    }

    @Test
    void equipementServiceUpdatesEquipmentFields() {
        IEquipementRepository repository = mock(IEquipementRepository.class);
        EquipementServiceImpl service = new EquipementServiceImpl(repository);
        Equipement existing = new Equipement();
        existing.setIdEquipement(4L);
        existing.setLibelle("GPS");
        Equipement update = new Equipement();
        update.setLibelle("Dash camera");
        when(repository.findById(4L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        assertSame(existing, service.update(4L, update));
        assertEquals("Dash camera", existing.getLibelle());
    }

    @Test
    void maintenanceServiceUpdatesMaintenanceFields() {
        IMaintenanceRepository repository = mock(IMaintenanceRepository.class);
        MaintenanceServiceImpl service = new MaintenanceServiceImpl(repository);
        Maintenance existing = new Maintenance();
        existing.setIdMaintenance(5L);
        existing.setDateDebut(LocalDate.of(2025, 2, 1));
        Maintenance update = new Maintenance();
        update.setDateDebut(LocalDate.of(2026, 2, 1));
        update.setDateFin(LocalDate.of(2026, 2, 2));
        update.setDescription("Inspection");
        when(repository.findById(5L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        assertSame(existing, service.update(5L, update));
        assertEquals(update.getDateDebut(), existing.getDateDebut());
        assertEquals(update.getDateFin(), existing.getDateFin());
        assertEquals("Inspection", existing.getDescription());
    }

    @Test
    void reservationServiceUpdatesReservationFields() {
        IReservationRepository repository = mock(IReservationRepository.class);
        ReservationServiceImpl service = new ReservationServiceImpl(repository);
        Reservation existing = new Reservation();
        existing.setIdReservation(6L);
        existing.setDateDebut(LocalDate.of(2025, 3, 1));
        existing.setDateFin(LocalDate.of(2025, 3, 2));
        Reservation update = new Reservation();
        update.setDateDebut(LocalDate.of(2026, 3, 1));
        update.setDateFin(LocalDate.of(2026, 3, 3));
        update.setStatut(StatutReservation.CONFIRMEE);
        when(repository.findById(6L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        assertSame(existing, service.update(6L, update));
        assertEquals(update.getDateDebut(), existing.getDateDebut());
        assertEquals(update.getDateFin(), existing.getDateFin());
        assertEquals(StatutReservation.CONFIRMEE, existing.getStatut());
    }

    @Test
    void contratServiceChecksRequiredFields() {
        ContratServiceImpl service = new ContratServiceImpl(mock(IContratRepository.class));
        Contrat contrat = new Contrat();
        contrat.setDateSignature(LocalDate.now());
        contrat.setMontantTotal(BigDecimal.TEN);

        assertTrue(service.check(contrat, true));
        assertFalse(service.check(new Contrat(), true));
        contrat.setDateSignature(null);
        assertFalse(service.check(contrat, true));
        contrat.setDateSignature(LocalDate.now());
        contrat.setMontantTotal(null);
        assertFalse(service.check(contrat, true));
    }

    @Test
    void employeServiceChecksRequiredFields() {
        EmployeServiceImpl service = new EmployeServiceImpl(mock(IEmployeRepository.class));

        assertTrue(service.check(validEmploye(), true));
        assertFalse(service.check(new Employe(), true));
        Employe missingRole = validEmploye();
        missingRole.setRole(null);
        assertFalse(service.check(missingRole, true));
    }

    @Test
    void equipementServiceChecksRequiredFields() {
        EquipementServiceImpl service = new EquipementServiceImpl(mock(IEquipementRepository.class));
        Equipement equipement = new Equipement();
        equipement.setLibelle("GPS");

        assertTrue(service.check(equipement, true));
        assertFalse(service.check(new Equipement(), true));
    }

    @Test
    void maintenanceServiceChecksRequiredFields() {
        MaintenanceServiceImpl service = new MaintenanceServiceImpl(mock(IMaintenanceRepository.class));
        Maintenance maintenance = new Maintenance();
        maintenance.setDateDebut(LocalDate.now());

        assertTrue(service.check(maintenance, true));
        assertFalse(service.check(new Maintenance(), true));
    }

    @Test
    void paiementServiceAllowsReadsAndRejectsWrites() {
        IPaiementRepository repository = mock(IPaiementRepository.class);
        PaiementServiceImpl service = new PaiementServiceImpl(repository);
        Paiement paiement = new Paiement();
        paiement.setIdPaiement(3L);
        when(repository.findById(3L)).thenReturn(Optional.of(paiement));
        when(repository.findAll()).thenReturn(List.of(paiement));

        assertSame(paiement, service.findById(3L));
        assertEquals(List.of(paiement), service.findAll());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(4L));
        assertThrows(UnsupportedOperationException.class, () -> service.create(paiement));
        assertThrows(UnsupportedOperationException.class, () -> service.update(3L, paiement));
        assertThrows(UnsupportedOperationException.class, () -> service.deleteById(3L));
    }

    @Test
    void reservationServiceChecksRequiredDates() {
        ReservationServiceImpl service = new ReservationServiceImpl(mock(IReservationRepository.class));
        Reservation reservation = new Reservation();
        reservation.setDateDebut(LocalDate.now());
        reservation.setDateFin(LocalDate.now().plusDays(1));

        assertTrue(service.check(reservation, true));
        assertFalse(service.check(new Reservation(), true));
        reservation.setDateFin(null);
        assertFalse(service.check(reservation, true));
        reservation.setDateFin(LocalDate.now().plusDays(1));
        reservation.setDateDebut(null);
        assertFalse(service.check(reservation, true));
    }

    @Test
    void vehiculeServiceValidatesPricesCalculatesOffersAndAssignsRelations() {
        IVehiculeRepository vehiculeRepository = mock(IVehiculeRepository.class);
        IAgenceRepository agenceRepository = mock(IAgenceRepository.class);
        IEquipementRepository equipementRepository = mock(IEquipementRepository.class);
        IOffreStrategy weekendStrategy = mock(IOffreStrategy.class);
        when(weekendStrategy.getOffre()).thenReturn(VehiculeOffre.WEEKEND);
        VehiculeServiceImpl service = new VehiculeServiceImpl(
                vehiculeRepository, List.of(weekendStrategy), agenceRepository, equipementRepository);
        Vehicule vehicule = validVehicule();
        Agence agence = validAgence();
        Equipement equipement = new Equipement();
        when(weekendStrategy.calculTarif(vehicule, 2)).thenReturn(160.0);
        when(vehiculeRepository.findById(5L)).thenReturn(Optional.of(vehicule));
        when(agenceRepository.findById(2L)).thenReturn(Optional.of(agence));
        when(equipementRepository.findById(3L)).thenReturn(Optional.of(equipement));

        assertTrue(service.check(vehicule, true));
        assertFalse(service.check(new Vehicule(), true));
        assertEquals(160.0, service.calculTarif(vehicule, 2, VehiculeOffre.WEEKEND), 0.001);
        assertEquals(300.0, service.calculTarif(vehicule, 3, null), 0.001);
        assertEquals(0.0, service.calculTarif((Vehicule) null, 3, null), 0.001);

        service.affecterAgence(5L, 2L);
        service.affecterEquipement(5L, 3L);
        assertSame(agence, vehicule.getAgence());
        assertTrue(vehicule.getEquipements().contains(equipement));
    }

    @Test
    void vehiculeServiceCalculatesPriceByIdAndClearsAgenceAssignments() {
        IVehiculeRepository vehiculeRepository = mock(IVehiculeRepository.class);
        VehiculeServiceImpl service = new VehiculeServiceImpl(
                vehiculeRepository, List.of(), mock(IAgenceRepository.class), mock(IEquipementRepository.class));
        Vehicule vehicule = validVehicule();
        vehicule.setAgence(validAgence());
        when(vehiculeRepository.findById(6L)).thenReturn(Optional.of(vehicule));

        assertEquals(200.0, service.calculTarif(6L, 2, VehiculeOffre.STANDARD), 0.001);
        service.desaffecterAgence(6L);
        assertNull(vehicule.getAgence());
        vehicule.setAgence(validAgence());
        service.desaffecterVehicule(6L);
        assertNull(vehicule.getAgence());
    }

    @Test
    void vehiculeServiceReportsMissingAgencyAndEquipmentAssignments() {
        IVehiculeRepository vehiculeRepository = mock(IVehiculeRepository.class);
        IAgenceRepository agenceRepository = mock(IAgenceRepository.class);
        IEquipementRepository equipementRepository = mock(IEquipementRepository.class);
        VehiculeServiceImpl service = new VehiculeServiceImpl(
                vehiculeRepository, List.of(), agenceRepository, equipementRepository);
        when(vehiculeRepository.findById(7L)).thenReturn(Optional.of(validVehicule()));

        assertThrows(ResourceNotFoundException.class, () -> service.affecterAgence(7L, 8L));
        assertThrows(ResourceNotFoundException.class, () -> service.affecterEquipement(7L, 9L));
    }

    @Test
    void vehiculeServiceUpdatesVehicleFields() {
        IVehiculeRepository repository = mock(IVehiculeRepository.class);
        VehiculeServiceImpl service = new VehiculeServiceImpl(
                repository, List.of(), mock(IAgenceRepository.class), mock(IEquipementRepository.class));
        Vehicule existing = validVehicule();
        existing.setIdVehicule(10L);
        Vehicule update = validVehicule();
        update.setMarque("Honda");
        update.setTarifJournalier(BigDecimal.valueOf(125));
        when(repository.findById(10L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        assertSame(existing, service.update(10L, update));
        assertEquals("Honda", existing.getMarque());
        assertEquals(BigDecimal.valueOf(125), existing.getTarifJournalier());
    }

    @Test
    void simulationServiceCalculatesAndReturnsConsoleReport() {
        IAgenceService agenceService = mock(IAgenceService.class);
        VehiculeServiceImpl vehiculeService = mock(VehiculeServiceImpl.class);
        SimulationConsoleDecorator service = new SimulationConsoleDecorator(agenceService, vehiculeService);
        Vehicule vehicule = validVehicule();
        Agence agence = validAgence();
        agence.setVehicules(Set.of(vehicule));
        when(agenceService.findById(1L)).thenReturn(agence);
        when(vehiculeService.calculTarif(vehicule, 2, VehiculeOffre.STANDARD)).thenReturn(40.0);

        assertEquals("40.0", service.simuler(1L, 2, VehiculeOffre.STANDARD));
        verify(vehiculeService).calculTarif(vehicule, 2, VehiculeOffre.STANDARD);
    }

    @Test
    void simulationServiceReturnsZeroWhenAgenceHasNoVehicles() {
        IAgenceService agenceService = mock(IAgenceService.class);
        VehiculeServiceImpl vehiculeService = mock(VehiculeServiceImpl.class);
        SimulationConsoleDecorator service = new SimulationConsoleDecorator(agenceService, vehiculeService);
        when(agenceService.findById(2L)).thenReturn(validAgence());

        assertEquals("0.0", service.simuler(2L, 3, VehiculeOffre.LONGUE_DUREE));
        verifyNoInteractions(vehiculeService);
    }

    private static Agence validAgence() {
        Agence agence = new Agence();
        agence.setNom("Agence Ariana");
        agence.setTelephone("71585874");
        agence.setAdresse("1 Rue Hedi");
        agence.setVille("Tunis");
        return agence;
    }

    private static Client validClient() {
        Client client = new Client();
        client.setNom("Nour");
        client.setPrenom("Ali");
        client.setEmail("nour@example.com");
        client.setNumPermis("TN123456");
        return client;
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
