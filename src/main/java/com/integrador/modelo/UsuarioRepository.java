package com.integrador.modelo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.integrador.tablas.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario,Long>{
	Usuario findByUsername(String username);
}
