package ru.rvr.notes.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public interface PersistenceRepository<T> {
    T save(T t);
    void remove(T t);
}
