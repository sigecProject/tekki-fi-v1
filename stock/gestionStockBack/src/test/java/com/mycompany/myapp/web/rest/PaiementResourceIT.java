package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.GestionStockBackApp;
import com.mycompany.myapp.domain.Paiement;
import com.mycompany.myapp.repository.PaiementRepository;
import com.mycompany.myapp.repository.search.PaiementSearchRepository;
import com.mycompany.myapp.service.PaiementService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link PaiementResource} REST controller.
 */
@SpringBootTest(classes = GestionStockBackApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class PaiementResourceIT {

    private static final Integer DEFAULT_NUM_TICKET = 1;
    private static final Integer UPDATED_NUM_TICKET = 2;

    private static final Integer DEFAULT_NUM_PAIEMENT = 1;
    private static final Integer UPDATED_NUM_PAIEMENT = 2;

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_MONTANT = 1D;
    private static final Double UPDATED_MONTANT = 2D;

    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private PaiementService paiementService;

    /**
     * This repository is mocked in the com.mycompany.myapp.repository.search test package.
     *
     * @see com.mycompany.myapp.repository.search.PaiementSearchRepositoryMockConfiguration
     */
    @Autowired
    private PaiementSearchRepository mockPaiementSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaiementMockMvc;

    private Paiement paiement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Paiement createEntity(EntityManager em) {
        Paiement paiement = new Paiement()
            .numTicket(DEFAULT_NUM_TICKET)
            .numPaiement(DEFAULT_NUM_PAIEMENT)
            .date(DEFAULT_DATE)
            .montant(DEFAULT_MONTANT);
        return paiement;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Paiement createUpdatedEntity(EntityManager em) {
        Paiement paiement = new Paiement()
            .numTicket(UPDATED_NUM_TICKET)
            .numPaiement(UPDATED_NUM_PAIEMENT)
            .date(UPDATED_DATE)
            .montant(UPDATED_MONTANT);
        return paiement;
    }

    @BeforeEach
    public void initTest() {
        paiement = createEntity(em);
    }

    @Test
    @Transactional
    public void createPaiement() throws Exception {
        int databaseSizeBeforeCreate = paiementRepository.findAll().size();

        // Create the Paiement
        restPaiementMockMvc.perform(post("/api/paiements")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(paiement)))
            .andExpect(status().isCreated());

        // Validate the Paiement in the database
        List<Paiement> paiementList = paiementRepository.findAll();
        assertThat(paiementList).hasSize(databaseSizeBeforeCreate + 1);
        Paiement testPaiement = paiementList.get(paiementList.size() - 1);
        assertThat(testPaiement.getNumTicket()).isEqualTo(DEFAULT_NUM_TICKET);
        assertThat(testPaiement.getNumPaiement()).isEqualTo(DEFAULT_NUM_PAIEMENT);
        assertThat(testPaiement.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testPaiement.getMontant()).isEqualTo(DEFAULT_MONTANT);

        // Validate the Paiement in Elasticsearch
        verify(mockPaiementSearchRepository, times(1)).save(testPaiement);
    }

    @Test
    @Transactional
    public void createPaiementWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = paiementRepository.findAll().size();

        // Create the Paiement with an existing ID
        paiement.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaiementMockMvc.perform(post("/api/paiements")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(paiement)))
            .andExpect(status().isBadRequest());

        // Validate the Paiement in the database
        List<Paiement> paiementList = paiementRepository.findAll();
        assertThat(paiementList).hasSize(databaseSizeBeforeCreate);

        // Validate the Paiement in Elasticsearch
        verify(mockPaiementSearchRepository, times(0)).save(paiement);
    }


    @Test
    @Transactional
    public void getAllPaiements() throws Exception {
        // Initialize the database
        paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList
        restPaiementMockMvc.perform(get("/api/paiements?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paiement.getId().intValue())))
            .andExpect(jsonPath("$.[*].numTicket").value(hasItem(DEFAULT_NUM_TICKET)))
            .andExpect(jsonPath("$.[*].numPaiement").value(hasItem(DEFAULT_NUM_PAIEMENT)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(DEFAULT_MONTANT.doubleValue())));
    }
    
    @Test
    @Transactional
    public void getPaiement() throws Exception {
        // Initialize the database
        paiementRepository.saveAndFlush(paiement);

        // Get the paiement
        restPaiementMockMvc.perform(get("/api/paiements/{id}", paiement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(paiement.getId().intValue()))
            .andExpect(jsonPath("$.numTicket").value(DEFAULT_NUM_TICKET))
            .andExpect(jsonPath("$.numPaiement").value(DEFAULT_NUM_PAIEMENT))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.montant").value(DEFAULT_MONTANT.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingPaiement() throws Exception {
        // Get the paiement
        restPaiementMockMvc.perform(get("/api/paiements/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePaiement() throws Exception {
        // Initialize the database
        paiementService.save(paiement);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockPaiementSearchRepository);

        int databaseSizeBeforeUpdate = paiementRepository.findAll().size();

        // Update the paiement
        Paiement updatedPaiement = paiementRepository.findById(paiement.getId()).get();
        // Disconnect from session so that the updates on updatedPaiement are not directly saved in db
        em.detach(updatedPaiement);
        updatedPaiement
            .numTicket(UPDATED_NUM_TICKET)
            .numPaiement(UPDATED_NUM_PAIEMENT)
            .date(UPDATED_DATE)
            .montant(UPDATED_MONTANT);

        restPaiementMockMvc.perform(put("/api/paiements")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedPaiement)))
            .andExpect(status().isOk());

        // Validate the Paiement in the database
        List<Paiement> paiementList = paiementRepository.findAll();
        assertThat(paiementList).hasSize(databaseSizeBeforeUpdate);
        Paiement testPaiement = paiementList.get(paiementList.size() - 1);
        assertThat(testPaiement.getNumTicket()).isEqualTo(UPDATED_NUM_TICKET);
        assertThat(testPaiement.getNumPaiement()).isEqualTo(UPDATED_NUM_PAIEMENT);
        assertThat(testPaiement.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testPaiement.getMontant()).isEqualTo(UPDATED_MONTANT);

        // Validate the Paiement in Elasticsearch
        verify(mockPaiementSearchRepository, times(1)).save(testPaiement);
    }

    @Test
    @Transactional
    public void updateNonExistingPaiement() throws Exception {
        int databaseSizeBeforeUpdate = paiementRepository.findAll().size();

        // Create the Paiement

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaiementMockMvc.perform(put("/api/paiements")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(paiement)))
            .andExpect(status().isBadRequest());

        // Validate the Paiement in the database
        List<Paiement> paiementList = paiementRepository.findAll();
        assertThat(paiementList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Paiement in Elasticsearch
        verify(mockPaiementSearchRepository, times(0)).save(paiement);
    }

    @Test
    @Transactional
    public void deletePaiement() throws Exception {
        // Initialize the database
        paiementService.save(paiement);

        int databaseSizeBeforeDelete = paiementRepository.findAll().size();

        // Delete the paiement
        restPaiementMockMvc.perform(delete("/api/paiements/{id}", paiement.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Paiement> paiementList = paiementRepository.findAll();
        assertThat(paiementList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Paiement in Elasticsearch
        verify(mockPaiementSearchRepository, times(1)).deleteById(paiement.getId());
    }

    @Test
    @Transactional
    public void searchPaiement() throws Exception {
        // Initialize the database
        paiementService.save(paiement);
        when(mockPaiementSearchRepository.search(queryStringQuery("id:" + paiement.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(paiement), PageRequest.of(0, 1), 1));
        // Search the paiement
        restPaiementMockMvc.perform(get("/api/_search/paiements?query=id:" + paiement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paiement.getId().intValue())))
            .andExpect(jsonPath("$.[*].numTicket").value(hasItem(DEFAULT_NUM_TICKET)))
            .andExpect(jsonPath("$.[*].numPaiement").value(hasItem(DEFAULT_NUM_PAIEMENT)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(DEFAULT_MONTANT.doubleValue())));
    }
}
