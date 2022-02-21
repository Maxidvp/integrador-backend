package com.integrador.services;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.integrador.tablas.Role;
import com.integrador.tablas.Usuario;

public interface IUsuarioService {
	
	Usuario saveUser(Usuario user);
	
	Role saveRole(Role role);
	
	void addRoleToUser(String username,String roleName);//Evitar usuarios duplicados
	
	Usuario getUser(String username);
	
	List<Usuario>getUsers();
	
	long existeUsername(String username);
	
	//String getUsernameById(Long id);
	String traerFotobyUsername(String username);
}
