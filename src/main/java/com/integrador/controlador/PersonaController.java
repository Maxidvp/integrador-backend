package com.integrador.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.integrador.modelo.IPersonasService;
import com.integrador.tablas.Personas;


@RestController
@RequestMapping("/personas")
@CrossOrigin(origins = "*")/*Revisar si se puede quitar en el servidor final*/
public class PersonaController {
	
	@Autowired
	private IPersonasService interPersona;
	
	@GetMapping ("/traer")
	//@ResponseBody
	public List<Personas> getPersona(){
		System.out.println("Estoy entraer todo");
		return interPersona.getPersona();
	}
	
	
	@GetMapping ("/buscar/{id}")
	public Personas serchPersona(@PathVariable Long id){
			System.out.println("Estoy entraer uno");
			return interPersona.findPersona(id);
	}
	
	@PostMapping ("/crear")
	public Personas createPersona(@RequestBody Personas persona){
		System.out.println("Estoy en crear");
		interPersona.savePersona(persona);
		return persona;
	}
	
	@DeleteMapping ("/borrar/{id}")
	public String deletePersona(@PathVariable Long id){
		interPersona.deletePersona(id);
		return "Tiene que ser texto";
	}
	
	@PutMapping ("/editar/{id}")
	public Personas editPersona (@PathVariable Long id, @RequestBody Personas persona){
		System.out.println("///////////////Esta en el editar///////////////");
		System.out.println("El id es = "+id);
		interPersona.savePersona(persona);
		return persona;
	}
}

