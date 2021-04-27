package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Administrateur;
import com.mycompany.myapp.service.AdministrateurService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Administrateur}.
 */
@RestController
@RequestMapping("/api")
public class AdministrateurResource {

    private final Logger log = LoggerFactory.getLogger(AdministrateurResource.class);

    private static final String ENTITY_NAME = "administrateur";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AdministrateurService administrateurService;

    public AdministrateurResource(AdministrateurService administrateurService) {
        this.administrateurService = administrateurService;
    }

    /**
     * {@code POST  /administrateurs} : Create a new administrateur.
     *
     * @param administrateur the administrateur to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new administrateur, or with status {@code 400 (Bad Request)} if the administrateur has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/administrateurs")
    public ResponseEntity<Administrateur> createAdministrateur(@RequestBody Administrateur administrateur) throws URISyntaxException {
        log.debug("REST request to save Administrateur : {}", administrateur);
        if (administrateur.getId() != null) {
            throw new BadRequestAlertException("A new administrateur cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Administrateur result = administrateurService.save(administrateur);
        return ResponseEntity.created(new URI("/api/administrateurs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /administrateurs} : Updates an existing administrateur.
     *
     * @param administrateur the administrateur to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated administrateur,
     * or with status {@code 400 (Bad Request)} if the administrateur is not valid,
     * or with status {@code 500 (Internal Server Error)} if the administrateur couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/administrateurs")
    public ResponseEntity<Administrateur> updateAdministrateur(@RequestBody Administrateur administrateur) throws URISyntaxException {
        log.debug("REST request to update Administrateur : {}", administrateur);
        if (administrateur.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Administrateur result = administrateurService.save(administrateur);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, administrateur.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /administrateurs} : get all the administrateurs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of administrateurs in body.
     */
    @GetMapping("/administrateurs")
    public ResponseEntity<List<Administrateur>> getAllAdministrateurs(Pageable pageable) {
        log.debug("REST request to get a page of Administrateurs");
        Page<Administrateur> page = administrateurService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /administrateurs/:id} : get the "id" administrateur.
     *
     * @param id the id of the administrateur to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the administrateur, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/administrateurs/{id}")
    public ResponseEntity<Administrateur> getAdministrateur(@PathVariable Long id) {
        log.debug("REST request to get Administrateur : {}", id);
        Optional<Administrateur> administrateur = administrateurService.findOne(id);
        return ResponseUtil.wrapOrNotFound(administrateur);
    }

    /**
     * {@code DELETE  /administrateurs/:id} : delete the "id" administrateur.
     *
     * @param id the id of the administrateur to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/administrateurs/{id}")
    public ResponseEntity<Void> deleteAdministrateur(@PathVariable Long id) {
        log.debug("REST request to delete Administrateur : {}", id);
        administrateurService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/administrateurs?query=:query} : search for the administrateur corresponding
     * to the query.
     *
     * @param query the query of the administrateur search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/administrateurs")
    public ResponseEntity<List<Administrateur>> searchAdministrateurs(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Administrateurs for query {}", query);
        Page<Administrateur> page = administrateurService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
