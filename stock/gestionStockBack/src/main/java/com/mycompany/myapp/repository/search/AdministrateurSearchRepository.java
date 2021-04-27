package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Administrateur;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Administrateur} entity.
 */
public interface AdministrateurSearchRepository extends ElasticsearchRepository<Administrateur, Long> {
}
