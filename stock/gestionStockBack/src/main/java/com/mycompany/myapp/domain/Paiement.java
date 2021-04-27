package com.mycompany.myapp.domain;


import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.Objects;
import java.time.LocalDate;

/**
 * A Paiement.
 */
@Entity
@Table(name = "paiement")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "paiement")
public class Paiement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "num_ticket")
    private Integer numTicket;

    @Column(name = "num_paiement")
    private Integer numPaiement;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "montant")
    private Double montant;

    @OneToOne
    @JoinColumn(unique = true)
    private Ticket ticket;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumTicket() {
        return numTicket;
    }

    public Paiement numTicket(Integer numTicket) {
        this.numTicket = numTicket;
        return this;
    }

    public void setNumTicket(Integer numTicket) {
        this.numTicket = numTicket;
    }

    public Integer getNumPaiement() {
        return numPaiement;
    }

    public Paiement numPaiement(Integer numPaiement) {
        this.numPaiement = numPaiement;
        return this;
    }

    public void setNumPaiement(Integer numPaiement) {
        this.numPaiement = numPaiement;
    }

    public LocalDate getDate() {
        return date;
    }

    public Paiement date(LocalDate date) {
        this.date = date;
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getMontant() {
        return montant;
    }

    public Paiement montant(Double montant) {
        this.montant = montant;
        return this;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public Paiement ticket(Ticket ticket) {
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
        if (!(o instanceof Paiement)) {
            return false;
        }
        return id != null && id.equals(((Paiement) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Paiement{" +
            "id=" + getId() +
            ", numTicket=" + getNumTicket() +
            ", numPaiement=" + getNumPaiement() +
            ", date='" + getDate() + "'" +
            ", montant=" + getMontant() +
            "}";
    }
}
