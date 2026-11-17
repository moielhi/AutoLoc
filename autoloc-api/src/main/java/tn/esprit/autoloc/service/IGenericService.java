package tn.esprit.autoloc.service;

public interface IGenericService<T, I> extends ICheckGenericService<T>, IReadGenericService<T, I>, IWriteGenericService<T, I> {
}
