package com.integrador.controlador;

import static java.util.Arrays.stream;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.integrador.repositories.UsuarioRepository;
import com.integrador.services.IPersonasService;
import com.integrador.services.IUsuarioService;
import com.integrador.services.UsuarioService;
import com.integrador.tablas.Educaciones;
import com.integrador.tablas.Experiencias;
import com.integrador.tablas.Habilidades;
import com.integrador.tablas.Personas;
import com.integrador.tablas.Proyectos;


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

	    //EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("org.springframework.boot");
	    //
		Authentication autoridades=SecurityContextHolder.getContext().getAuthentication();
		Object principal = autoridades.getPrincipal();
		Personas persona = new Personas();
		Personas duenio = new Personas();
		Long idPrincipal=obtenerId(principal.toString());
		//EntityManager lookupEm =entityManager.getEntityManagerFactory().createEntityManager();
		//aList.forEach(a -> em.detach(a));
		//EntityManager entityManager = null;
		/*EntityManagerFactory emf = new entityManager.getEntityManagerFactory().createEntityManager();
		EntityManager em = emf.createEntityManager();*/
		
		if(accion==1) {
			System.out.println("Copia del autor");
			duenio=interPersona.findPersona(1L);
			
			persona.setId(idPrincipal);
			//Con esto vacio las tablas y bindeo la entidad
			persona=interPersona.savePersona(persona);

			//Desconecto las entidades de cada lista
			for(Educaciones educacion : duenio.getEducaciones()){
				this.entityManager.detach(educacion);
			    educacion.setId(null);
			}				

			for(Experiencias experiencia : duenio.getExperiencias()){
				this.entityManager.detach(experiencia);
				experiencia.setId(null);
			}

			for(Habilidades habilidad : duenio.getHabilidades()){
				this.entityManager.detach(habilidad);
				habilidad.setId(null);
			}

			
			for(Proyectos proyecto : duenio.getProyectos()){
				this.entityManager.detach(proyecto);
				proyecto.setId(null);
			}
			
			//Desconecto la entidad principal
			this.entityManager.detach(duenio);
			//Copio sus valores a persona
			persona=duenio;
			//Le reasigno el id del usuario
			persona.setId(idPrincipal);	
			System.out.println(persona);
			/*System.out.println("Copia del autor");
			duenio=interPersona.findPersona(1L);
			persona.setId(idPrincipal);
			persona=interPersona.savePersona(persona);
			persona.setApellido(duenio.getApellido());
			persona.setDireccion(duenio.getDireccion());
			persona.setEdad(duenio.getEdad());
			persona.setEmail(duenio.getEmail());
			persona.setNombre(duenio.getNombre());
			persona.setSrc(duenio.getSrc());
			persona.setTelefono(duenio.getTelefono());
			
			List<Educaciones> educaciones=new ArrayList<Educaciones>();
			for(Educaciones educacion : duenio.getEducaciones()){
				this.entityManager.detach(educacion);
			    educacion.setId(null);
			    educaciones.add(educacion);
			}				
			persona.setEducaciones(educaciones);

			List<Experiencias> experiencias=new ArrayList<Experiencias>();
			for(Experiencias experiencia : duenio.getExperiencias()){
				this.entityManager.detach(experiencia);
				experiencia.setId(null);
				experiencias.add(experiencia);
			}
			persona.setExperiencias(experiencias);

			List<Habilidades> habilidades=new ArrayList<Habilidades>();
			for(Habilidades habilidad : duenio.getHabilidades()){
				this.entityManager.detach(habilidad);
				habilidad.setId(null);
				habilidades.add(habilidad);
			}
			persona.setHabilidades(habilidades);
			
			List<Proyectos> proyectos=new ArrayList<Proyectos>();
			for(Proyectos proyecto : duenio.getProyectos()){
				this.entityManager.detach(proyecto);
				proyecto.setId(null);
				proyectos.add(proyecto);
			}
			persona.setProyectos(proyectos);
			

			persona.setId(idPrincipal);		
			System.out.println(persona);*/
		}else {
			System.out.println("Nuevo y vacio");
			persona.setApellido("Apellido");
			persona.setNombre("Nombre");
			persona.setId(idPrincipal);
		}
		return interPersona.savePersona(persona);
		
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
	
	@PutMapping ("/editar")
	public Personas editPersona (@RequestBody Personas persona){
		System.out.println("///////////////Esta en el editar///////////////");
		Personas personaEntity;

		Authentication autoridades=SecurityContextHolder.getContext().getAuthentication();
		Object principal = autoridades.getPrincipal();
		
		if(autoridades != null && autoridades.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"))) {
			//Si se es admin se puede editar cualquier porfolio
			//implementar getpersona sin principal para obtener el ususraio correspondiente
			personaEntity=persona;
		}else {
			//Si no se es admin todos los cambios son agregados al profolio asociado al usuario
			Long idPrincipal = obtenerId(principal.toString());
			personaEntity = interPersona.findPersona(idPrincipal);
			personaEntity = persona;//En teoria reescribir los id producirioa una error en los jpa
			//persona.setId(idPrincipal);
		}
		return interPersona.savePersona(personaEntity);
		
		/*
		String userDB=obtenerUsername(persona.getId());
		System.out.println("Principal:");
		System.out.println(principal);
		System.out.println("UserDB:");
		System.out.println(userDB);
		System.out.println("UserJSON:");
		System.out.println(persona.getUsername());
		//Para editar, el JSON del front y el objetivo en la base de datos deben pertenecer al usuario logueado
		if(principal.equals(userDB) && principal.equals(persona.getUsername())) {
			interPersona.savePersona(persona);
			return persona;
		}else {
	        return null;//No tiene permisos para realizar los cambios
		}*/
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

