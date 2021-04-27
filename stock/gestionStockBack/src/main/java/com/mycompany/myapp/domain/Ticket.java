package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Ticket.
 */
@Entity
@Table(name = "ticket")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "ticket")
public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prix_total")
    private Float prixTotal;

    @Column(name = "product_id")
    private Integer productId;

    @OneToOne
    @JoinColumn(unique = true)
    private Client client;

    @OneToOne(mappedBy = "ticket")
    @JsonIgnore
    private Paiement paiement;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getPrixTotal() {
        return prixTotal;
    }

    public Ticket prixTotal(Float prixTotal) {
        this.prixTotal = prixTotal;
        return this;
    }

    public void setPrixTotal(Float prixTotal) {
        this.prixTotal = prixTotal;
    }

    public Integer getProductId() {
        return productId;
    }

    public Ticket productId(Integer productId) {
        this.productId = productId;
        return this;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Client getClient() {
        return client;
    }

    public Ticket client(Client client) {
        this.client = client;
        return this;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Paiement getPaiement() {
        return paiement;
    }

    public Ticket paiement(Paiement paiement) {
        this.paiement = paiement;
        return this;
    }

    public void setPaiement(Paiement paiement) {
        this.paiement = paiement;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ticket)) {
            return false;
        }
        return id != null && id.equals(((Ticket) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Ticket{" +
            "id=" + getId() +
            ", prixTotal=" + getPrixTotal() +
            ", productId=" + getProductId() +
            "}";
    }
}
