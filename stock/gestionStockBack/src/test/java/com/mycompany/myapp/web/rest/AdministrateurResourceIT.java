package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.GestionStockBackApp;
import com.mycompany.myapp.domain.Administrateur;
import com.mycompany.myapp.repository.AdministrateurRepository;
import com.mycompany.myapp.repository.search.AdministrateurSearchRepository;
import com.mycompany.myapp.service.AdministrateurService;

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
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link AdministrateurResource} REST controller.
 */
@SpringBootTest(classes = GestionStockBackApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class AdministrateurResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    @Autowired
    private AdministrateurRepository administrateurRepository;

    @Autowired
    private AdministrateurService administrateurService;

    /**
     * This repository is mocked in the com.mycompany.myapp.repository.search test package.
     *
     * @see com.mycompany.myapp.repository.search.AdministrateurSearchRepositoryMockConfiguration
     */
    @Autowired
    private AdministrateurSearchRepository mockAdministrateurSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAdministrateurMockMvc;

    private Administrateur administrateur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Administrateur createEntity(EntityManager em) {
        Administrateur administrateur = new Administrateur()
            .name(DEFAULT_NAME)
            .prenom(DEFAULT_PRENOM)
            .telephone(DEFAULT_TELEPHONE);
        return administrateur;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Administrateur createUpdatedEntity(EntityManager em) {
        Administrateur administrateur = new Administrateur()
            .name(UPDATED_NAME)
            .prenom(UPDATED_PRENOM)
            .telephone(UPDATED_TELEPHONE);
        return administrateur;
    }

    @BeforeEach
    public void initTest() {
        administrateur = createEntity(em);
    }

    @Test
    @Transactional
    public void createAdministrateur() throws Exception {
        int databaseSizeBeforeCreate = administrateurRepository.findAll().size();

        // Create the Administrateur
        restAdministrateurMockMvc.perform(post("/api/administrateurs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(administrateur)))
            .andExpect(status().isCreated());

        // Validate the Administrateur in the database
        List<Administrateur> administrateurList = administrateurRepository.findAll();
        assertThat(administrateurList).hasSize(databaseSizeBeforeCreate + 1);
        Administrateur testAdministrateur = administrateurList.get(administrateurList.size() - 1);
        assertThat(testAdministrateur.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAdministrateur.getPrenom()).isEqualTo(DEFAULT_PRENOM);
        assertThat(testAdministrateur.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);

        // Validate the Administrateur in Elasticsearch
        verify(mockAdministrateurSearchRepository, times(1)).save(testAdministrateur);
    }

    @Test
    @Transactional
    public void createAdministrateurWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = administrateurRepository.findAll().size();

        // Create the Administrateur with an existing ID
        administrateur.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAdministrateurMockMvc.perform(post("/api/administrateurs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(administrateur)))
            .andExpect(status().isBadRequest());

        // Validate the Administrateur in the database
        List<Administrateur> administrateurList = administrateurRepository.findAll();
        assertThat(administrateurList).hasSize(databaseSizeBeforeCreate);

        // Validate the Administrateur in Elasticsearch
        verify(mockAdministrateurSearchRepository, times(0)).save(administrateur);
    }


    @Test
    @Transactional
    public void getAllAdministrateurs() throws Exception {
        // Initialize the database
        administrateurRepository.saveAndFlush(administrateur);

        // Get all the administrateurList
        restAdministrateurMockMvc.perform(get("/api/administrateurs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(administrateur.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)));
    }
    
    @Test
    @Transactional
    public void getAdministrateur() throws Exception {
        // Initialize the database
        administrateurRepository.saveAndFlush(administrateur);

        // Get the administrateur
        restAdministrateurMockMvc.perform(get("/api/administrateurs/{id}", administrateur.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(administrateur.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE));
    }

    @Test
    @Transactional
    public void getNonExistingAdministrateur() throws Exception {
        // Get the administrateur
        restAdministrateurMockMvc.perform(get("/api/administrateurs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAdministrateur() throws Exception {
        // Initialize the database
        administrateurService.save(administrateur);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockAdministrateurSearchRepository);

        int databaseSizeBeforeUpdate = administrateurRepository.findAll().size();

        // Update the administrateur
        Administrateur updatedAdministrateur = administrateurRepository.findById(administrateur.getId()).get();
        // Disconnect from session so that the updates on updatedAdministrateur are not directly saved in db
        em.detach(updatedAdministrateur);
        updatedAdministrateur
            .name(UPDATED_NAME)
            .prenom(UPDATED_PRENOM)
            .telephone(UPDATED_TELEPHONE);

        restAdministrateurMockMvc.perform(put("/api/administrateurs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedAdministrateur)))
            .andExpect(status().isOk());

        // Validate the Administrateur in the database
        List<Administrateur> administrateurList = administrateurRepository.findAll();
        assertThat(administrateurList).hasSize(databaseSizeBeforeUpdate);
        Administrateur testAdministrateur = administrateurList.get(administrateurList.size() - 1);
        assertThat(testAdministrateur.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAdministrateur.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testAdministrateur.getTelephone()).isEqualTo(UPDATED_TELEPHONE);

        // Validate the Administrateur in Elasticsearch
        verify(mockAdministrateurSearchRepository, times(1)).save(testAdministrateur);
    }

    @Test
    @Transactional
    public void updateNonExistingAdministrateur() throws Exception {
        int databaseSizeBeforeUpdate = administrateurRepository.findAll().size();

        // Create the Administrateur

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAdministrateurMockMvc.perform(put("/api/administrateurs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(administrateur)))
            .andExpect(status().isBadRequest());

        // Validate the Administrateur in the database
        List<Administrateur> administrateurList = administrateurRepository.findAll();
        assertThat(administrateurList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Administrateur in Elasticsearch
        verify(mockAdministrateurSearchRepository, times(0)).save(administrateur);
    }

    @Test
    @Transactional
    public void deleteAdministrateur() throws Exception {
        // Initialize the database
        administrateurService.save(administrateur);

        int databaseSizeBeforeDelete = administrateurRepository.findAll().size();

        // Delete the administrateur
        restAdministrateurMockMvc.perform(delete("/api/administrateurs/{id}", administrateur.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Administrateur> administrateurList = administrateurRepository.findAll();
        assertThat(administrateurList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Administrateur in Elasticsearch
        verify(mockAdministrateurSearchRepository, times(1)).deleteById(administrateur.getId());
    }

    @Test
    @Transactional
    public void searchAdministrateur() throws Exception {
        // Initialize the database
        administrateurService.save(administrateur);
        when(mockAdministrateurSearchRepository.search(queryStringQuery("id:" + administrateur.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(administrateur), PageRequest.of(0, 1), 1));
        // Search the administrateur
        restAdministrateurMockMvc.perform(get("/api/_search/administrateurs?query=id:" + administrateur.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(administrateur.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)));
    }
}
