package tn.esprit.autoloc.service;

import org.springframework.stereotype.Service;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.repository.IClientRepository;

@Service
public class ClientServiceImpl extends GenericServiceImpl<Client, Long> {

    public ClientServiceImpl(IClientRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityName() {
        return "Client";
    }

    @Override
    protected Long getId(Client entity) {
        return entity.getIdClient();
    }

    @Override
    protected void setFields(Client existing, Client entity) {
        existing.setNom(entity.getNom());
        existing.setPrenom(entity.getPrenom());
        existing.setEmail(entity.getEmail());
        existing.setTelephone(entity.getTelephone());
        existing.setNumPermis(entity.getNumPermis());
        existing.setDateInscription(entity.getDateInscription());
        existing.setReservations(entity.getReservations());
    }

    @Override
    public boolean check(Client entity, boolean isNew) {
        return entity != null
                && Utils.isNotEmpty(entity.getNom())
                && Utils.isNotEmpty(entity.getPrenom())
                && Utils.isNotEmpty(entity.getEmail())
                && Utils.isNotEmpty(entity.getNumPermis());
    }
}
