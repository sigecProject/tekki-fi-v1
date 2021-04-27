package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Administrateur;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Administrateur entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AdministrateurRepository extends JpaRepository<Administrateur, Long> {
}
