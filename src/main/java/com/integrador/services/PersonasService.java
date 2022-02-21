package com.integrador.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.integrador.repositories.PersonasRepository;
import com.integrador.repositories.UsuarioRepository;
import com.integrador.tablas.Personas;

@Service
public class PersonasService implements IPersonasService{
	
	@Autowired
	private PersonasRepository personaRepository;
	
	@Override
	public List<Personas> getPersona() {
		List<Personas> listaPersonas = personaRepository.findAll();
		return listaPersonas;
	}

	@Override
	public Personas savePersona(Personas persona) {
		return personaRepository.save(persona);
	}

	@Override
	public void deletePersona(Long id) {
		personaRepository.deleteById(id);
	}

	@Override
	public Personas findPersona(Long id) {
		Personas persona = personaRepository.findById(id).orElse(null);
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
}
