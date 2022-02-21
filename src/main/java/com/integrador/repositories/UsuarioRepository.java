package com.integrador.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.integrador.tablas.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario,Long>{
	
	Usuario findByUsername(String username);
	
	long countByUsername(String username);
	
	@Query(value = "SELECT `foto` FROM `personas` WHERE id=(SELECT `persona_id` FROM `usuario` WHERE username=?1)", nativeQuery = true)
	String getFotobyUsername(String username);
	
	/*@Query(value = "SELECT `username` FROM `usuario` WHERE id=?1", nativeQuery = true)
	String getUsernameById(Long id);*/
}