package tn.esprit.autoloc.service;

public interface ICheckGenericService<T> {
    boolean check(T entity, boolean isNew);
}
