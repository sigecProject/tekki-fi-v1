package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Administrateur;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Administrateur}.
 */
public interface AdministrateurService {

    /**
     * Save a administrateur.
     *
     * @param administrateur the entity to save.
     * @return the persisted entity.
     */
    Administrateur save(Administrateur administrateur);

    /**
     * Get all the administrateurs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Administrateur> findAll(Pageable pageable);

    /**
     * Get the "id" administrateur.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Administrateur> findOne(Long id);

    /**
     * Delete the "id" administrateur.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the administrateur corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Administrateur> search(String query, Pageable pageable);
}
