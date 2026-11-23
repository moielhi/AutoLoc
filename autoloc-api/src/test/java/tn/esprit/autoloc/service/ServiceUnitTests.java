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
        ClientServiceImpl service = new ClientServiceImpl(mock(IClientRepository.class));

        assertThrows(IllegalArgumentException.class, () -> service.create(new Client()));
        assertThrows(ResourceNotFoundException.class, () -> service.findById(9L));
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
    void clientServiceChecksRequiredFields() {
        ClientServiceImpl service = new ClientServiceImpl(mock(IClientRepository.class));

        assertTrue(service.check(validClient(), true));
        assertFalse(service.check(new Client(), true));
    }

    @Test
    void contratServiceChecksRequiredFields() {
        ContratServiceImpl service = new ContratServiceImpl(mock(IContratRepository.class));
        Contrat contrat = new Contrat();
        contrat.setDateSignature(LocalDate.now());
        contrat.setMontantTotal(BigDecimal.TEN);

        assertTrue(service.check(contrat, true));
        assertFalse(service.check(new Contrat(), true));
    }

    @Test
    void employeServiceChecksRequiredFields() {
        EmployeServiceImpl service = new EmployeServiceImpl(mock(IEmployeRepository.class));

        assertTrue(service.check(validEmploye(), true));
        assertFalse(service.check(new Employe(), true));
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

        assertSame(paiement, service.findById(3L));
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
