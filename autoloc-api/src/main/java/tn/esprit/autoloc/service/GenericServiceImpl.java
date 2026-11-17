package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.exception.ResourceNotFoundException;

import java.util.List;

@RequiredArgsConstructor
public abstract class GenericServiceImpl<T, I> implements IGenericService<T, I> {

    protected final JpaRepository<T, I> repository;

    protected abstract String getEntityName();
    protected abstract I getId(T entity);
    protected abstract void setFields(T existing, T entity);

    @Override
    public boolean check(T entity, boolean isNew) {
        return entity != null;
    }

    @Override
    public T create(T entity) {
        if (getId(entity) != null) {
            throw new IllegalArgumentException("Identifiant non null !!!!!");
        }
        if (!check(entity, true)) {
            throw new IllegalArgumentException("Certains champs sont invalides !");
        }
        return repository.save(entity);
    }

    @Override
    public T findById(I id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getEntityName(), id.toString()));
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public T update(I id, T entity) {
        T existing = findById(id);
        if (!check(entity, false)) {
            throw new IllegalArgumentException("Certains champs sont invalides !");
        }
        setFields(existing, entity);
        return repository.save(existing);
    }

    @Override
    public void deleteById(I id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(getEntityName(), id.toString());
        }
        repository.deleteById(id);
    }
}
