package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.service.PaiementService;
import com.mycompany.myapp.domain.Paiement;
import com.mycompany.myapp.repository.PaiementRepository;
import com.mycompany.myapp.repository.search.PaiementSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Paiement}.
 */
@Service
@Transactional
public class PaiementServiceImpl implements PaiementService {

    private final Logger log = LoggerFactory.getLogger(PaiementServiceImpl.class);

    private final PaiementRepository paiementRepository;

    private final PaiementSearchRepository paiementSearchRepository;

    public PaiementServiceImpl(PaiementRepository paiementRepository, PaiementSearchRepository paiementSearchRepository) {
        this.paiementRepository = paiementRepository;
        this.paiementSearchRepository = paiementSearchRepository;
    }

    /**
     * Save a paiement.
     *
     * @param paiement the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Paiement save(Paiement paiement) {
        log.debug("Request to save Paiement : {}", paiement);
        Paiement result = paiementRepository.save(paiement);
        paiementSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the paiements.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Paiement> findAll(Pageable pageable) {
        log.debug("Request to get all Paiements");
        return paiementRepository.findAll(pageable);
    }

    /**
     * Get one paiement by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Paiement> findOne(Long id) {
        log.debug("Request to get Paiement : {}", id);
        return paiementRepository.findById(id);
    }

    /**
     * Delete the paiement by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Paiement : {}", id);
        paiementRepository.deleteById(id);
        paiementSearchRepository.deleteById(id);
    }

    /**
     * Search for the paiement corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Paiement> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Paiements for query {}", query);
        return paiementSearchRepository.search(queryStringQuery(query), pageable);    }
}
