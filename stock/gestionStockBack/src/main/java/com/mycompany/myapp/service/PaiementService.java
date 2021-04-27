package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Paiement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Paiement}.
 */
public interface PaiementService {

    /**
     * Save a paiement.
     *
     * @param paiement the entity to save.
     * @return the persisted entity.
     */
    Paiement save(Paiement paiement);

    /**
     * Get all the paiements.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Paiement> findAll(Pageable pageable);

    /**
     * Get the "id" paiement.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Paiement> findOne(Long id);

    /**
     * Delete the "id" paiement.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the paiement corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Paiement> search(String query, Pageable pageable);
}
