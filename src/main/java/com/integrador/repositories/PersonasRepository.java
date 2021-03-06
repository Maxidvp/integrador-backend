package com.integrador.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integrador.tablas.Persona;

@Repository
public interface PersonasRepository extends JpaRepository<Persona, Long>{
	@Query(value = "SELECT username FROM personas WHERE id=?1", nativeQuery = true)
	String getUsernameById(Long id);
	
	@Query(value = "SELECT id FROM usuario WHERE username=?1", nativeQuery = true)
	Long getIdByUsername(String username);

}

