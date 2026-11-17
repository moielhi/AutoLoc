package tn.esprit.autoloc.service;

import org.springframework.stereotype.Service;
import tn.esprit.autoloc.Utils;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.repository.IEquipementRepository;

@Service
public class EquipementServiceImpl extends GenericServiceImpl<Equipement, Long> {

    public EquipementServiceImpl(IEquipementRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityName() {
        return "Equipement";
    }

    @Override
    protected Long getId(Equipement entity) {
        return entity.getIdEquipement();
    }

    @Override
    protected void setFields(Equipement existing, Equipement entity) {
        existing.setLibelle(entity.getLibelle());
        existing.setVehicules(entity.getVehicules());
    }

    @Override
    public boolean check(Equipement entity, boolean isNew) {
        return entity != null && Utils.isNotEmpty(entity.getLibelle());
    }
}
