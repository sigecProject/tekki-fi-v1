package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.service.AdministrateurService;
import com.mycompany.myapp.domain.Administrateur;
import com.mycompany.myapp.repository.AdministrateurRepository;
import com.mycompany.myapp.repository.search.AdministrateurSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Administrateur}.
 */
@Service
@Transactional
public class AdministrateurServiceImpl implements AdministrateurService {

    private final Logger log = LoggerFactory.getLogger(AdministrateurServiceImpl.class);

    private final AdministrateurRepository administrateurRepository;

    private final AdministrateurSearchRepository administrateurSearchRepository;

    public AdministrateurServiceImpl(AdministrateurRepository administrateurRepository, AdministrateurSearchRepository administrateurSearchRepository) {
        this.administrateurRepository = administrateurRepository;
        this.administrateurSearchRepository = administrateurSearchRepository;
    }

    /**
     * Save a administrateur.
     *
     * @param administrateur the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Administrateur save(Administrateur administrateur) {
        log.debug("Request to save Administrateur : {}", administrateur);
        Administrateur result = administrateurRepository.save(administrateur);
        administrateurSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the administrateurs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Administrateur> findAll(Pageable pageable) {
        log.debug("Request to get all Administrateurs");
        return administrateurRepository.findAll(pageable);
    }

    /**
     * Get one administrateur by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Administrateur> findOne(Long id) {
        log.debug("Request to get Administrateur : {}", id);
        return administrateurRepository.findById(id);
    }

    /**
     * Delete the administrateur by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Administrateur : {}", id);
        administrateurRepository.deleteById(id);
        administrateurSearchRepository.deleteById(id);
    }

    /**
     * Search for the administrateur corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Administrateur> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Administrateurs for query {}", query);
        return administrateurSearchRepository.search(queryStringQuery(query), pageable);    }
}
