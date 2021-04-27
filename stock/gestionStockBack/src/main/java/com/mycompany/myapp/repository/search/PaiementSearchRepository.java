package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Paiement;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Paiement} entity.
 */
public interface PaiementSearchRepository extends ElasticsearchRepository<Paiement, Long> {
}
