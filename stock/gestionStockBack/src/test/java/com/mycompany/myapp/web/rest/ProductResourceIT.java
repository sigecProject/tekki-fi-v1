package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.GestionStockBackApp;
import com.mycompany.myapp.domain.Product;
import com.mycompany.myapp.repository.ProductRepository;
import com.mycompany.myapp.repository.search.ProductSearchRepository;
import com.mycompany.myapp.service.ProductService;

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
 * Integration tests for the {@link ProductResource} REST controller.
 */
@SpringBootTest(classes = GestionStockBackApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class ProductResourceIT {

    private static final String DEFAULT_PRODUCT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCT_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PRODUCT_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCT_CATEGORY = "BBBBBBBBBB";

    private static final Integer DEFAULT_PRIX_ACHAT = 1;
    private static final Integer UPDATED_PRIX_ACHAT = 2;

    private static final Integer DEFAULT_PRIX_VENTE = 1;
    private static final Integer UPDATED_PRIX_VENTE = 2;

    private static final Boolean DEFAULT_DISPONIBLE = false;
    private static final Boolean UPDATED_DISPONIBLE = true;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    /**
     * This repository is mocked in the com.mycompany.myapp.repository.search test package.
     *
     * @see com.mycompany.myapp.repository.search.ProductSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProductSearchRepository mockProductSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductMockMvc;

    private Product product;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createEntity(EntityManager em) {
        Product product = new Product()
            .productCode(DEFAULT_PRODUCT_CODE)
            .name(DEFAULT_NAME)
            .productCategory(DEFAULT_PRODUCT_CATEGORY)
            .prixAchat(DEFAULT_PRIX_ACHAT)
            .prixVente(DEFAULT_PRIX_VENTE)
            .disponible(DEFAULT_DISPONIBLE);
        return product;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createUpdatedEntity(EntityManager em) {
        Product product = new Product()
            .productCode(UPDATED_PRODUCT_CODE)
            .name(UPDATED_NAME)
            .productCategory(UPDATED_PRODUCT_CATEGORY)
            .prixAchat(UPDATED_PRIX_ACHAT)
            .prixVente(UPDATED_PRIX_VENTE)
            .disponible(UPDATED_DISPONIBLE);
        return product;
    }

    @BeforeEach
    public void initTest() {
        product = createEntity(em);
    }

    @Test
    @Transactional
    public void createProduct() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();

        // Create the Product
        restProductMockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isCreated());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate + 1);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getProductCode()).isEqualTo(DEFAULT_PRODUCT_CODE);
        assertThat(testProduct.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProduct.getProductCategory()).isEqualTo(DEFAULT_PRODUCT_CATEGORY);
        assertThat(testProduct.getPrixAchat()).isEqualTo(DEFAULT_PRIX_ACHAT);
        assertThat(testProduct.getPrixVente()).isEqualTo(DEFAULT_PRIX_VENTE);
        assertThat(testProduct.isDisponible()).isEqualTo(DEFAULT_DISPONIBLE);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(1)).save(testProduct);
    }

    @Test
    @Transactional
    public void createProductWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();

        // Create the Product with an existing ID
        product.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductMockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(0)).save(product);
    }


    @Test
    @Transactional
    public void getAllProducts() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList
        restProductMockMvc.perform(get("/api/products?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].productCode").value(hasItem(DEFAULT_PRODUCT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].productCategory").value(hasItem(DEFAULT_PRODUCT_CATEGORY)))
            .andExpect(jsonPath("$.[*].prixAchat").value(hasItem(DEFAULT_PRIX_ACHAT)))
            .andExpect(jsonPath("$.[*].prixVente").value(hasItem(DEFAULT_PRIX_VENTE)))
            .andExpect(jsonPath("$.[*].disponible").value(hasItem(DEFAULT_DISPONIBLE.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get the product
        restProductMockMvc.perform(get("/api/products/{id}", product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(product.getId().intValue()))
            .andExpect(jsonPath("$.productCode").value(DEFAULT_PRODUCT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.productCategory").value(DEFAULT_PRODUCT_CATEGORY))
            .andExpect(jsonPath("$.prixAchat").value(DEFAULT_PRIX_ACHAT))
            .andExpect(jsonPath("$.prixVente").value(DEFAULT_PRIX_VENTE))
            .andExpect(jsonPath("$.disponible").value(DEFAULT_DISPONIBLE.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingProduct() throws Exception {
        // Get the product
        restProductMockMvc.perform(get("/api/products/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProduct() throws Exception {
        // Initialize the database
        productService.save(product);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockProductSearchRepository);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product
        Product updatedProduct = productRepository.findById(product.getId()).get();
        // Disconnect from session so that the updates on updatedProduct are not directly saved in db
        em.detach(updatedProduct);
        updatedProduct
            .productCode(UPDATED_PRODUCT_CODE)
            .name(UPDATED_NAME)
            .productCategory(UPDATED_PRODUCT_CATEGORY)
            .prixAchat(UPDATED_PRIX_ACHAT)
            .prixVente(UPDATED_PRIX_VENTE)
            .disponible(UPDATED_DISPONIBLE);

        restProductMockMvc.perform(put("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedProduct)))
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getProductCode()).isEqualTo(UPDATED_PRODUCT_CODE);
        assertThat(testProduct.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProduct.getProductCategory()).isEqualTo(UPDATED_PRODUCT_CATEGORY);
        assertThat(testProduct.getPrixAchat()).isEqualTo(UPDATED_PRIX_ACHAT);
        assertThat(testProduct.getPrixVente()).isEqualTo(UPDATED_PRIX_VENTE);
        assertThat(testProduct.isDisponible()).isEqualTo(UPDATED_DISPONIBLE);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(1)).save(testProduct);
    }

    @Test
    @Transactional
    public void updateNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Create the Product

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc.perform(put("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(0)).save(product);
    }

    @Test
    @Transactional
    public void deleteProduct() throws Exception {
        // Initialize the database
        productService.save(product);

        int databaseSizeBeforeDelete = productRepository.findAll().size();

        // Delete the product
        restProductMockMvc.perform(delete("/api/products/{id}", product.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(1)).deleteById(product.getId());
    }

    @Test
    @Transactional
    public void searchProduct() throws Exception {
        // Initialize the database
        productService.save(product);
        when(mockProductSearchRepository.search(queryStringQuery("id:" + product.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(product), PageRequest.of(0, 1), 1));
        // Search the product
        restProductMockMvc.perform(get("/api/_search/products?query=id:" + product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].productCode").value(hasItem(DEFAULT_PRODUCT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].productCategory").value(hasItem(DEFAULT_PRODUCT_CATEGORY)))
            .andExpect(jsonPath("$.[*].prixAchat").value(hasItem(DEFAULT_PRIX_ACHAT)))
            .andExpect(jsonPath("$.[*].prixVente").value(hasItem(DEFAULT_PRIX_VENTE)))
            .andExpect(jsonPath("$.[*].disponible").value(hasItem(DEFAULT_DISPONIBLE.booleanValue())));
    }
}
