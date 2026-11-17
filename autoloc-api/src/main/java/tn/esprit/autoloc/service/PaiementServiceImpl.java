package tn.esprit.autoloc.service;

import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.repository.IPaiementRepository;

@Service
public class PaiementServiceImpl extends GenericServiceImpl<Paiement, Long> implements IPaiementService {

    public PaiementServiceImpl(IPaiementRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityName() {
        return "Paiement";
    }

    @Override
    protected Long getId(Paiement entity) {
        return entity.getIdPaiement();
    }

    @Override
    protected void setFields(Paiement existing, Paiement entity) {
        throw new UnsupportedOperationException("Le service Paiement est en lecture seule");
    }

    @Override
    public Paiement create(Paiement entity) {
        throw new UnsupportedOperationException("Le service Paiement est en lecture seule");
    }

    @Override
    public Paiement update(Long id, Paiement entity) {
        throw new UnsupportedOperationException("Le service Paiement est en lecture seule");
    }

    @Override
    public void deleteById(Long id) {
        throw new UnsupportedOperationException("Le service Paiement est en lecture seule");
    }
}
