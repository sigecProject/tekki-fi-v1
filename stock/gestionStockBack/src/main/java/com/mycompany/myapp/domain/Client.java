package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

/**
 * A Client.
 */
@Entity
@Table(name = "client")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "client")
public class Client implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_ticket")
    private Integer idTicket;

    @ManyToMany
    @JoinTable(name = "client_product",
               joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
    private Set<Product> products = new HashSet<>();

    @OneToOne(mappedBy = "client")
    @JsonIgnore
    private Ticket ticket;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdTicket() {
        return idTicket;
    }

    public Client idTicket(Integer idTicket) {
        this.idTicket = idTicket;
        return this;
    }

    public void setIdTicket(Integer idTicket) {
        this.idTicket = idTicket;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public Client products(Set<Product> products) {
        this.products = products;
        return this;
    }

    public Client addProduct(Product product) {
        this.products.add(product);
        product.getClients().add(this);
        return this;
    }

    public Client removeProduct(Product product) {
        this.products.remove(product);
        product.getClients().remove(this);
        return this;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public Client ticket(Ticket ticket) {
        this.ticket = ticket;
        return this;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Client)) {
            return false;
        }
        return id != null && id.equals(((Client) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Client{" +
            "id=" + getId() +
            ", idTicket=" + getIdTicket() +
            "}";
    }
}
