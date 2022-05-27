package com.integrador.services;

import java.util.List;

import com.integrador.tablas.Persona;

public interface IPersonasService {
	
	// metodo para traer todas las personas
	public List<Persona> getPersona();
	
	//metodo para dar de alta una persona
	public Persona savePersona (Persona persona);
	
	//metodo para borrar una persona
	public void deletePersona (Long id);
	
	//metodo para encontrar una persona
	public Persona findPersona (Long id);
	
	String getUsernameById(Long id);
	
	Long getIdByUsername(String username);

	//List<Long> idsLista(String tabla,String fk,Long id);
}
