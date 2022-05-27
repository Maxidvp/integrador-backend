package com.integrador.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.integrador.repositories.PersonasRepository;
import com.integrador.tablas.Persona;

@Service
public class PersonasService implements IPersonasService{
	
	@Autowired
	private PersonasRepository personaRepository;
	
	@Override
	public List<Persona> getPersona() {
		List<Persona> listaPersonas = personaRepository.findAll();
		return listaPersonas;
	}

	@Override
	public Persona savePersona(Persona persona) {
		return personaRepository.save(persona);
	}

	@Override
	public void deletePersona(Long id) {
		personaRepository.deleteById(id);
	}

	@Override
	public Persona findPersona(Long id) {
		Persona persona = personaRepository.findById(id).orElse(null);
		return persona;
	}

	@Override
	public String getUsernameById(Long id) {
		return personaRepository.getUsernameById(id);
	}

	@Override
	public Long getIdByUsername(String username) {
		return personaRepository.getIdByUsername(username);
	}

	/*@Override
	public List<Long> idsLista(String tabla, String fk, Long id) {
		return personaRepository.idsList(tabla, fk, id);
	}*/
}
