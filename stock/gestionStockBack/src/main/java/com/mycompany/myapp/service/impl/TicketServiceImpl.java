package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.service.TicketService;
import com.mycompany.myapp.domain.Ticket;
import com.mycompany.myapp.repository.TicketRepository;
import com.mycompany.myapp.repository.search.TicketSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Ticket}.
 */
@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private final Logger log = LoggerFactory.getLogger(TicketServiceImpl.class);

    private final TicketRepository ticketRepository;

    private final TicketSearchRepository ticketSearchRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, TicketSearchRepository ticketSearchRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketSearchRepository = ticketSearchRepository;
    }

    /**
     * Save a ticket.
     *
     * @param ticket the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Ticket save(Ticket ticket) {
        log.debug("Request to save Ticket : {}", ticket);
        Ticket result = ticketRepository.save(ticket);
        ticketSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the tickets.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Ticket> findAll(Pageable pageable) {
        log.debug("Request to get all Tickets");
        return ticketRepository.findAll(pageable);
    }


    /**
     *  Get all the tickets where Paiement is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true) 
    public List<Ticket> findAllWherePaiementIsNull() {
        log.debug("Request to get all tickets where Paiement is null");
        return StreamSupport
            .stream(ticketRepository.findAll().spliterator(), false)
            .filter(ticket -> ticket.getPaiement() == null)
            .collect(Collectors.toList());
    }

    /**
     * Get one ticket by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Ticket> findOne(Long id) {
        log.debug("Request to get Ticket : {}", id);
        return ticketRepository.findById(id);
    }

    /**
     * Delete the ticket by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Ticket : {}", id);
        ticketRepository.deleteById(id);
        ticketSearchRepository.deleteById(id);
    }

    /**
     * Search for the ticket corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Ticket> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Tickets for query {}", query);
        return ticketSearchRepository.search(queryStringQuery(query), pageable);    }
}
