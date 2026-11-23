package tn.esprit.autoloc.web.controller;

import org.junit.jupiter.api.Test;
import tn.esprit.autoloc.calcul.VehiculeOffre;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.service.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class ControllerUnitTests {

    @Test
    void clientControllerReturnsTheServiceList() {
        ClientServiceImpl service = mock(ClientServiceImpl.class);
        List<Client> clients = List.of(new Client());
        when(service.findAll()).thenReturn(clients);

        assertSame(clients, new ClientController(service).findAll());
    }

    @Test
    void clientControllerDelegatesFindById() {
        ClientServiceImpl service = mock(ClientServiceImpl.class);
        Client client = new Client();
        when(service.findById(1L)).thenReturn(client);

        assertSame(client, new ClientController(service).findById(1L));
        verify(service).findById(1L);
    }

    @Test
    void clientControllerDelegatesCreate() {
        ClientServiceImpl service = mock(ClientServiceImpl.class);
        Client client = new Client();
        when(service.create(client)).thenReturn(client);

        assertSame(client, new ClientController(service).create(client));
        verify(service).create(client);
    }

    @Test
    void clientControllerDelegatesUpdate() {
        ClientServiceImpl service = mock(ClientServiceImpl.class);
        Client client = new Client();
        when(service.update(2L, client)).thenReturn(client);

        assertSame(client, new ClientController(service).update(2L, client));
        verify(service).update(2L, client);
    }

    @Test
    void clientControllerDelegatesDelete() {
        ClientServiceImpl service = mock(ClientServiceImpl.class);

        new ClientController(service).deleteById(3L);

        verify(service).deleteById(3L);
    }

    @Test
    void paiementControllerReturnsReadOnlyList() {
        PaiementServiceImpl service = mock(PaiementServiceImpl.class);
        List<Paiement> paiements = List.of(new Paiement());
        when(service.findAll()).thenReturn(paiements);

        assertSame(paiements, new PaiementController(service).findAll());
    }

    @Test
    void paiementControllerDelegatesFindById() {
        PaiementServiceImpl service = mock(PaiementServiceImpl.class);
        Paiement paiement = new Paiement();
        when(service.findById(4L)).thenReturn(paiement);

        assertSame(paiement, new PaiementController(service).findById(4L));
        verify(service).findById(4L);
    }

    @Test
    void agenceControllerDelegatesNestedEmployeeUpdate() {
        IAgenceService service = mock(IAgenceService.class);
        ISimulationService simulationService = mock(ISimulationService.class);
        Agence agence = new Agence();
        when(service.updateAvecEmployes(5L, agence)).thenReturn(agence);

        assertSame(agence, new AgenceController(service, simulationService).updateAvecEmployes(5L, agence));
        verify(service).updateAvecEmployes(5L, agence);
    }

    @Test
    void agenceControllerDefaultsSimulationToOneDay() {
        IAgenceService service = mock(IAgenceService.class);
        ISimulationService simulationService = mock(ISimulationService.class);
        when(simulationService.simuler(6L, 1, VehiculeOffre.STANDARD)).thenReturn("report");

        String report = new AgenceController(service, simulationService)
                .simuler(6L, null, null, VehiculeOffre.STANDARD);

        assertEquals("report", report);
        verify(simulationService).simuler(6L, 1, VehiculeOffre.STANDARD);
    }

    @Test
    void agenceControllerPrefersJoursOverAliasWhenBothAreProvided() {
        IAgenceService service = mock(IAgenceService.class);
        ISimulationService simulationService = mock(ISimulationService.class);
        AgenceController controller = new AgenceController(service, simulationService);

        controller.simuler(6L, 3, 9, VehiculeOffre.WEEKEND);

        verify(simulationService).simuler(6L, 3, VehiculeOffre.WEEKEND);
    }

    @Test
    void agenceControllerUsesNbJoursAliasWhenJoursIsAbsent() {
        IAgenceService service = mock(IAgenceService.class);
        ISimulationService simulationService = mock(ISimulationService.class);
        AgenceController controller = new AgenceController(service, simulationService);

        controller.simuler(6L, null, 4, VehiculeOffre.LONGUE_DUREE);

        verify(simulationService).simuler(6L, 4, VehiculeOffre.LONGUE_DUREE);
    }

    @Test
    void vehiculeControllerDelegatesAgencyDeassignmentDeleteAlias() {
        VehiculeServiceImpl service = mock(VehiculeServiceImpl.class);

        new VehiculeController(service).desaffecterAgenceDelete(7L);

        verify(service).desaffecterAgence(7L);
    }

    @Test
    void vehiculeControllerDelegatesAgencyAssignment() {
        VehiculeServiceImpl service = mock(VehiculeServiceImpl.class);

        new VehiculeController(service).affecterAgence(7L, 8L);

        verify(service).affecterAgence(7L, 8L);
    }

    @Test
    void vehiculeControllerDelegatesEquipmentAssignment() {
        VehiculeServiceImpl service = mock(VehiculeServiceImpl.class);

        new VehiculeController(service).affecterEquipement(7L, 9L);

        verify(service).affecterEquipement(7L, 9L);
    }
}
