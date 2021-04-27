package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

/**
 * A Product.
 */
@Entity
@Table(name = "product")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "name")
    private String name;

    @Column(name = "product_category")
    private String productCategory;

    @Column(name = "prix_achat")
    private Integer prixAchat;

    @Column(name = "prix_vente")
    private Integer prixVente;

    @Column(name = "disponible")
    private Boolean disponible;

    @ManyToOne
    @JsonIgnoreProperties("products")
    private Category category;

    @ManyToMany(mappedBy = "products")
    @JsonIgnore
    private Set<Client> clients = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public Product productCode(String productCode) {
        this.productCode = productCode;
        return this;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getName() {
        return name;
    }

    public Product name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public Product productCategory(String productCategory) {
        this.productCategory = productCategory;
        return this;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public Integer getPrixAchat() {
        return prixAchat;
    }

    public Product prixAchat(Integer prixAchat) {
        this.prixAchat = prixAchat;
        return this;
    }

    public void setPrixAchat(Integer prixAchat) {
        this.prixAchat = prixAchat;
    }

    public Integer getPrixVente() {
        return prixVente;
    }

    public Product prixVente(Integer prixVente) {
        this.prixVente = prixVente;
        return this;
    }

    public void setPrixVente(Integer prixVente) {
        this.prixVente = prixVente;
    }

    public Boolean isDisponible() {
        return disponible;
    }

    public Product disponible(Boolean disponible) {
        this.disponible = disponible;
        return this;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public Category getCategory() {
        return category;
    }

    public Product category(Category category) {
        this.category = category;
        return this;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<Client> getClients() {
        return clients;
    }

    public Product clients(Set<Client> clients) {
        this.clients = clients;
        return this;
    }

    public Product addClient(Client client) {
        this.clients.add(client);
        client.getProducts().add(this);
        return this;
    }

    public Product removeClient(Client client) {
        this.clients.remove(client);
        client.getProducts().remove(this);
        return this;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", productCode='" + getProductCode() + "'" +
            ", name='" + getName() + "'" +
            ", productCategory='" + getProductCategory() + "'" +
            ", prixAchat=" + getPrixAchat() +
            ", prixVente=" + getPrixVente() +
            ", disponible='" + isDisponible() + "'" +
            "}";
    }
}
