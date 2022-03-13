package com.integrador.controlador;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.integrador.tablas.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.integrador.services.IPersonasService;


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
		System.out.println("SearchPersona:");
		if(id != 1) {
			Long idPrincipal=obtenerId(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
			System.out.println("El id del usuario es:");
			System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
			System.out.println(idPrincipal);
			id=idPrincipal;
		}
		System.out.println(id);
		return interPersona.findPersona(id);
	}
	
	
	@Autowired
    private EntityManager entityManager;	
		
	@GetMapping ("/instancia/{accion}")
	public Personas instanciaPersona(@PathVariable int accion){
		//Aparentemente algunos metodos afectan la entidad y la DB en simultaneo por con
		//la metodologia Lazy por lo que se observan asincronismos en la consola

		Personas personaVacia = new Personas();
		Personas personaDuenio;

		//Utilizo los metodos de java para obtener el username de la persona logueada
		Authentication autoridades=SecurityContextHolder.getContext().getAuthentication();
		Object principal = autoridades.getPrincipal();

		//Obtengo la entidad asociada a la sesion
		Long idPrincipal=obtenerId(principal.toString());
		Personas personaUsuario=interPersona.findPersona(idPrincipal);

		//Limpia las tablas correspondientes a las listas
		personaUsuario.getEducaciones().clear();
		personaUsuario.getExperiencias().clear();
		personaUsuario.getHabilidades().clear();
		personaUsuario.getProyectos().clear();
		personaUsuario.getRedes().clear();
		//interPersona.savePersona(personaUsuario);

		if(accion==1) {
			System.out.println("Copia del autor");
			//Obtengo la entidad del autor
			personaDuenio=interPersona.findPersona(1L);

			//Copia de los literales
			personaUsuario.setId(idPrincipal);
			personaUsuario.setNombre(personaDuenio.getNombre());
			personaUsuario.setApellido(personaDuenio.getApellido());
			personaUsuario.setTitulo(personaDuenio.getTitulo());
			personaUsuario.setDireccion(personaDuenio.getDireccion());
			personaUsuario.setFoto(personaDuenio.getFoto());
			personaUsuario.setBanner(personaDuenio.getBanner());
			personaUsuario.setNacimiento(personaDuenio.getNacimiento());
			personaUsuario.setTelefono(personaDuenio.getTelefono());
			personaUsuario.setEmail(personaDuenio.getEmail());
			personaUsuario.setSobremi(personaDuenio.getSobremi());

			//Desconecto las entidades de cada lista y agrego cada elemento a la entidad del usuario
			for(Educaciones educacion : personaDuenio.getEducaciones()){
				this.entityManager.detach(educacion);
			    educacion.setId(null);
				personaUsuario.getEducaciones().add(educacion);
			}
			for(Experiencias experiencia : personaDuenio.getExperiencias()){
				this.entityManager.detach(experiencia);
				experiencia.setId(null);
				personaUsuario.getExperiencias().add(experiencia);
			}
			for(Habilidades habilidad : personaDuenio.getHabilidades()){
				this.entityManager.detach(habilidad);
				habilidad.setId(null);
				personaUsuario.getHabilidades().add(habilidad);
			}
			for(Proyectos proyecto : personaDuenio.getProyectos()){
				this.entityManager.detach(proyecto);
				proyecto.setId(null);
				personaUsuario.getProyectos().add(proyecto);
			}
			for(Redes red : personaDuenio.getRedes()){
				this.entityManager.detach(red);
				red.setId(null);
				personaUsuario.getRedes().add(red);
			}

		}else {
			//interPersona.savePersona(new Personas(idPrincipal,"Lala","Lalaland",null,null,null,null,null,new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>()));

			System.out.println("Nuevo y vacio");
			//Creo una nueva entidad casi vacia
			personaUsuario=new Personas(
					idPrincipal,
					"Nombre",
					"Apellido",
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					new ArrayList<>(),
					new ArrayList<>(),
					new ArrayList<>(),
					new ArrayList<>(),
					new ArrayList<>()
			);
		}

		System.out.println("Asi quedo la persona: ");
		System.out.println(personaUsuario);
		return interPersona.savePersona(personaUsuario);
		
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

	private Boolean misID(List<Long> idJSON, List<Long> idDB){
		System.out.println("idJSON es:");
		System.out.println(idJSON);
		System.out.println("idDB es:");
		System.out.println(idDB);
		for(Long id : idJSON){
			if(id!=null && !idDB.contains(id) ){
				//Se encontro un problema de seguridad
				System.out.println("Se detecto un problema de seguridad");
				return false;
			}
		}
		return true;
	}

	@PutMapping ("/editar")
	public Personas editPersona (@RequestBody Personas persona){
		System.out.println("///////////////Esta en el editar///////////////");

		Authentication autoridades=SecurityContextHolder.getContext().getAuthentication();
		Object principal = autoridades.getPrincipal();

		//Si no se es admin se debe realizar comprobaciones
		if(!autoridades.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"))) {//autoridades != null &&
			//Se verifica el id del JSON con profolio asociado al usuario logueado
			Long idPrincipal = obtenerId(principal.toString());
			Personas personaDB = interPersona.findPersona(idPrincipal);
			if(persona.getId()!=personaDB.getId()){
				System.out.println("Se detecto un problema de seguridad");
				return null;
			}

			//Obtengo la lista de educaciones a guardar, si contiene elementos se procede a verificar
			List<Educaciones> educaciones=persona.getEducaciones();
			if(educaciones.size()>0){
				//Obtengo una lista de los ids del JSON recibido
				List<Long> idJSON = persona.getEducaciones().stream().map(x -> x.getId()).collect(Collectors.toList());
				//Obtengo una lista de los ids existentes en la DB
				List<Long> idDB = personaDB.getEducaciones().stream().map(x -> x.getId()).collect(Collectors.toList());
				//Si se detecta que se esta por editar un elemento con un id que no le pertenece se termina la ejecucion
				if(!misID(idJSON,idDB)){return null;}
			}

			//Repito para las otras entidades

			if(persona.getExperiencias().size()>0){
				List<Long> idJSON = persona.getExperiencias().stream().map(x -> x.getId()).collect(Collectors.toList());
				List<Long> idDB = personaDB.getExperiencias().stream().map(x -> x.getId()).collect(Collectors.toList());
				if(!misID(idJSON,idDB)){return null;}
			}

			if(persona.getHabilidades().size()>0){
				List<Long> idJSON = persona.getHabilidades().stream().map(x -> x.getId()).collect(Collectors.toList());
				List<Long> idDB = personaDB.getHabilidades().stream().map(x -> x.getId()).collect(Collectors.toList());
				if(!misID(idJSON,idDB)){return null;}
			}

			if(persona.getProyectos().size()>0){
				List<Long> idJSON = persona.getProyectos().stream().map(x -> x.getId()).collect(Collectors.toList());
				List<Long> idDB = personaDB.getProyectos().stream().map(x -> x.getId()).collect(Collectors.toList());
				if(!misID(idJSON,idDB)){return null;}
			}

			if(persona.getRedes().size()>0){
				List<Long> idJSON = persona.getRedes().stream().map(x -> x.getId()).collect(Collectors.toList());
				List<Long> idDB = personaDB.getRedes().stream().map(x -> x.getId()).collect(Collectors.toList());
				if(!misID(idJSON,idDB)){return null;}
			}
		}
		return interPersona.savePersona(persona);

	}
	
	@GetMapping("/usernamebyid/{id}")
	public String obtenerUsername(@PathVariable Long id){
		System.out.println("obtenerUsername");
		return interPersona.getUsernameById(id);
	}
	
	private Long obtenerId(String username){
		System.out.println("obtenerId");
		return interPersona.getIdByUsername(username);
	}
}

