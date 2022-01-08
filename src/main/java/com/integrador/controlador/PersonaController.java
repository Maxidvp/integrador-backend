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
import org.springframework.web.bind.annotation.RestController;

import com.integrador.modelo.IPersonasService;
import com.integrador.tablas.Personas;


@RestController
@CrossOrigin(origins = "*")/*Revisar si se puede quitar en el servidor final*/
public class PersonaController {
	
	@Autowired
	private IPersonasService interPersona;
	
	@GetMapping ("/personas/traer")
	//@ResponseBody
	public List<Personas> getPersona(){
		System.out.println("Estoy entraer todo");
		return interPersona.getPersona();
	}
	
	
	@GetMapping ("/personas/buscar/{id}")
	public Personas serchPersona(@PathVariable Long id){
			System.out.println("Estoy entraer uno");
			return interPersona.findPersona(id);
	}
	
	@PostMapping ("/personas/crear")
	public Personas createPersona(@RequestBody Personas persona){
		System.out.println("Estoy en crear");
		interPersona.savePersona(persona);
		return persona;
	}
	
	@DeleteMapping ("/personas/borrar/{id}")
	public String deletePersona(@PathVariable Long id){
		interPersona.deletePersona(id);
		
			
		return "Tiene que ser texto";
	}
	
	@PutMapping ("/personas/editar/{id}")
	public String editPersona (@PathVariable Long id, @RequestBody Personas persona){
		System.out.println("El id es = "+id);
		//busco la persona en cuestion
		/*Persona persona = interPersona.findPersona(id);
		
		//esto tambien pude ir en service
		//para desacoplar mejor aun el codigo en un nuevo metodo
		persona.setApellido(nuevoApellido);
		persona.setNombre(nuevoNombre);
		persona.setEdad(nuevaEdad);*/

		interPersona.savePersona(persona);
		return "{}";
	}
	
	/*@PutMapping ("/personas/editar/{id}")
	public String editPersona (@PathVariable Long id,
							@RequestParam ("nombre") String nuevoNombre,
							@RequestParam ("apellido") String nuevoApellido,
							@RequestParam ("edad") int nuevaEdad){
		System.out.println("Estoy en editar");
		//busco la persona en cuestion
		Persona persona = interPersona.findPersona(id);
		
		//esto tambien pude ir en service
		//para desacoplar mejor aun el codigo en un nuevo metodo
		persona.setApellido(nuevoApellido);
		persona.setNombre(nuevoNombre);
		persona.setEdad(nuevaEdad);
		
		interPersona.savePersona(persona);
		return "{}";
	}*/
}

