package com.integrador.modelo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integrador.tablas.Personas;

@Repository
public interface PersonasRepository extends JpaRepository<Personas, Long>{

}

