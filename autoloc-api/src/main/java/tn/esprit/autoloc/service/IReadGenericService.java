package tn.esprit.autoloc.service;

import java.util.List;
//test
public interface IReadGenericService<T, I> {
    T findById(I id);
    List<T> findAll();
}
