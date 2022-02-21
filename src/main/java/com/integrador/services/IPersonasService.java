package com.integrador.services;

import java.util.List;

import com.integrador.tablas.Personas;

public interface IPersonasService {
	
	// metodo para traer todas las personas
	public List<Personas> getPersona();
	
	//metodo para dar de alta una persona
	public Personas savePersona (Personas persona);
	
	//metodo para borrar una persona
	public void deletePersona (Long id);
	
	//metodo para encontrar una persona
	public Personas findPersona (Long id);
	
	String getUsernameById(Long id);
	
	Long getIdByUsername(String username);
}