package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Ticket;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Ticket} entity.
 */
public interface TicketSearchRepository extends ElasticsearchRepository<Ticket, Long> {
}
