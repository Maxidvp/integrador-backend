package com.integrador.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.integrador.tablas.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario,Long>{
	
	Usuario findByUsername(String username);
	
	long countByUsername(String username);

	long countByEmail(String email);
	
	@Query(value = "SELECT foto FROM resumen WHERE id=(SELECT id FROM usuario WHERE username=?1)", nativeQuery = true)
	String getFotobyUsername(String username);

	@Query(value = "SELECT username FROM usuario WHERE email=?1", nativeQuery = true)
	String getUsernamebyEmail(String email);

}