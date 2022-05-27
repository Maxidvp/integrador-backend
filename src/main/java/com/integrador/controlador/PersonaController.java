package com.integrador.controlador;

import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.integrador.tablas.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequestMapping("/persona")
@RequiredArgsConstructor
public class PersonaController {

	@Autowired
	private IPersonasService interPersona;

	@Autowired
	private EntityManager entityManager;

	@GetMapping ("/publico/{username}")
	public Persona personaPublica(@PathVariable String username){
		System.out.println("PersonaContoller - personaPublica:");
		Long id=obtenerId(username);
		System.out.println("El id es:");
		System.out.println(id);
		if(id != null){
			Persona persona = interPersona.findPersona(id);
			if(persona.isPublico()){
				System.out.println("En el if true");
				return persona;
			}else{
				System.out.println("En el if false");
				return null;
			}
		}else {
			return null;
		}

	}
	
	@GetMapping ("/buscar/{id}")
	public Persona serchPersona(@PathVariable Long id){
		if(id != 1) {
			Long idPrincipal=obtenerId(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
			id=idPrincipal;
		}
		System.out.println("PersonaContoller - SearchPersona:");
		System.out.println(id);
		return interPersona.findPersona(id);
	}

	@PostMapping ("/crear")
	public Persona createPersona(@RequestBody Persona persona){
		System.out.println("PersonaContoller - createPersona");
		interPersona.savePersona(persona);
		return persona;
	}

	@PostMapping ("/agregar/{tipo}")
	public Persona agregarElemento(@PathVariable String tipo, @RequestBody Persona persona){
		System.out.println("PersonaContoller - agregarElemento");
		Authentication autoridades=SecurityContextHolder.getContext().getAuthentication();
		Object principal = autoridades.getPrincipal();

		//Obtengo la entidad asociada a la sesion
		Long idPrincipal=obtenerId(principal.toString());
		//Long idPrincipal=6L;
		Persona personaDB=interPersona.findPersona(idPrincipal);

		Long idPersona = null;
		if(tipo.equals("educaciones")){
			Educacion educacion=persona.getEducaciones().get(0);
			idPersona=educacion.getId();
			if(idPersona==null){
				personaDB.getEducaciones().add(educacion);
			}
		}else if(tipo.equals("experiencias")){
			Experiencia experiencia=persona.getExperiencias().get(0);
			idPersona=experiencia.getId();
			if(idPersona==null){
				personaDB.getExperiencias().add(experiencia);
			}
		}else if(tipo.equals("habilidades")){
			Habilidad habilidad=persona.getHabilidades().get(0);
			idPersona=habilidad.getId();
			if(idPersona==null){
				personaDB.getHabilidades().add(habilidad);
			}
		}else if(tipo.equals("proyectos")){
			Proyecto proyecto=persona.getProyectos().get(0);
			idPersona=proyecto.getId();
			if(idPersona==null){
				personaDB.getProyectos().add(proyecto);
			}
		}

		if(idPersona==null){
			System.out.println("Se agrego correctamente - PersonaContoller - agregarElemento");
			return interPersona.savePersona(personaDB);
		}else {
			System.out.println("Se detecto un problema de seguridad - PersonaContoller - agregarElemento");
			return null;
		}
	}

	@PutMapping ("/editar/{tipo}")
	public Persona editarElemento(@PathVariable String tipo, @RequestBody Persona persona){
		System.out.println("PersonaContoller - editarElemento");
		System.out.println(persona);
		Authentication autoridades=SecurityContextHolder.getContext().getAuthentication();
		Object principal = autoridades.getPrincipal();

		//Obtengo la entidad asociada a la sesion
		Long idPrincipal=obtenerId(principal.toString());
		//Long idPrincipal=6L;
		Persona personaDB=interPersona.findPersona(idPrincipal);
		List<Long> idsDB = null;
		Long idPersona = null;
		Boolean todoOK=false;

		if(tipo.equals("banner")){
			if( verificarFoto(persona.getResumen().getBanner()) ){
				personaDB.getResumen().setBanner(persona.getResumen().getBanner());
				todoOK=true;
			}else{
				todoOK=false;
			}
		}else if(tipo.equals("resumen")){
			personaDB.setResumen(persona.getResumen());
			personaDB.getResumen().setId(idPrincipal);
			todoOK=true;
		}else if(tipo.equals("educaciones")){
			Educacion educacion=persona.getEducaciones().get(0);
			idPersona=educacion.getId();
			idsDB = personaDB.getEducaciones().stream().map(x -> x.getId()).collect(Collectors.toList());
			if(idsDB!=null && idsDB.contains(idPersona)){
				personaDB.getEducaciones().set(idsDB.indexOf(idPersona),educacion);
				todoOK=true;
				if( !verificarFoto(educacion.getImagen()) ){
					todoOK=false;
				}
			}
		}else if(tipo.equals("experiencias")){
			Experiencia experiencia=persona.getExperiencias().get(0);
			idPersona=experiencia.getId();
			idsDB = personaDB.getExperiencias().stream().map(x -> x.getId()).collect(Collectors.toList());
			if(idsDB!=null && idsDB.contains(idPersona)){
				personaDB.getExperiencias().set(idsDB.indexOf(idPersona),experiencia);
				todoOK=true;
				if( !verificarFoto(experiencia.getImagen()) ){
					todoOK=false;
				}
			}
		}else if(tipo.equals("habilidades")){
			Habilidad habilidad=persona.getHabilidades().get(0);
			idPersona=habilidad.getId();
			idsDB = personaDB.getHabilidades().stream().map(x -> x.getId()).collect(Collectors.toList());
			if(idsDB!=null && idsDB.contains(idPersona)){
				personaDB.getHabilidades().set(idsDB.indexOf(idPersona),habilidad);
				todoOK=true;
			}
		}else if(tipo.equals("proyectos")){
			Proyecto proyecto=persona.getProyectos().get(0);
			idPersona=proyecto.getId();
			idsDB = personaDB.getProyectos().stream().map(x -> x.getId()).collect(Collectors.toList());
			System.out.println("proyecto");
			System.out.println(proyecto);
			if(idsDB!=null && idsDB.contains(idPersona)){
				personaDB.getProyectos().set(idsDB.indexOf(idPersona),proyecto);
				todoOK=true;
				for(String foto: proyecto.getFotos()){
					if( !verificarFoto(foto) ){
						todoOK=false;
					}
				}
			}
		}else if(tipo.equals("redes")){
			//personaDB.setRedes(persona.getRedes());
			List<Redes> redes=persona.getRedes();
			List<Redes> redesDB=personaDB.getRedes();
			if(redes.size()>0){
				List<Long> idJSON = redes.stream().map(x -> x.getId()).collect(Collectors.toList());
				List<Long> idDB = personaDB.getRedes().stream().map(x -> x.getId()).collect(Collectors.toList());
				if(misID(idJSON,idDB)){
					personaDB.getRedes().clear();
					for(Redes red:redes){
						redesDB.add(red);
					}
					todoOK=true;
				}
			}
		}

		if(todoOK==true){
			Persona respuesta=interPersona.savePersona(personaDB);
			System.out.println("Se edito correctamente - PersonaContoller - editarElemento");
			return respuesta;
		}else {
			System.out.println("Se detecto un problema de seguridad - PersonaContoller - editarElemento 206");
			return null;
		}
	}

	private boolean verificarFoto(String foto){
		System.out.println("Foto");
		System.out.println(foto);
		System.out.println(foto==null || foto.length()==0 || Pattern.compile(".+\\.(jpg|jpeg|png|webp|avif|gif|svg)").matcher(foto).matches());
		return foto==null || foto.length()==0 || Pattern.compile(".+\\.(jpg|jpeg|png|webp|avif|gif|svg)").matcher(foto).matches();
	}

	@DeleteMapping ("/eliminar/{tipo}/{id}")
	public Persona eliminarElemento(@PathVariable String tipo, @PathVariable Long id){
		System.out.println("PersonaContoller - eliminarContenido");
		Authentication autoridades=SecurityContextHolder.getContext().getAuthentication();
		Object principal = autoridades.getPrincipal();

		//Obtengo la entidad asociada a la sesion
		Long idPrincipal=obtenerId(principal.toString());
		//Long idPrincipal=6L;
		Persona personaUsuario=interPersona.findPersona(idPrincipal);

		//Verifico si el elemento a eliminar es propiedad del ususario
		List<Long> misIDs = null;
		if(tipo.equals("educaciones")){
			//Obtengo la lista de ids que posee el ususario
			misIDs = personaUsuario.getEducaciones().stream().map(x -> x.getId()).collect(Collectors.toList());
			//Verifico que posea elementos y que uno coincide con el que se quiere eliminar
			if(misIDs!=null && misIDs.contains(id)){
				//Elimino el elemento por posicion
				personaUsuario.getEducaciones().remove(misIDs.indexOf(id));
			}
		}else if(tipo.equals("experiencias")){
			misIDs = personaUsuario.getExperiencias().stream().map(x -> x.getId()).collect(Collectors.toList());
			if(misIDs!=null && misIDs.contains(id)){
				personaUsuario.getExperiencias().remove(misIDs.indexOf(id));
			}
		}else if(tipo.equals("habilidades")){
			misIDs = personaUsuario.getHabilidades().stream().map(x -> x.getId()).collect(Collectors.toList());
			if(misIDs!=null && misIDs.contains(id)){
				personaUsuario.getHabilidades().remove(misIDs.indexOf(id));
			}
		}else if(tipo.equals("proyectos")){
			misIDs = personaUsuario.getProyectos().stream().map(x -> x.getId()).collect(Collectors.toList());
			if(misIDs!=null && misIDs.contains(id)){
				personaUsuario.getProyectos().remove(misIDs.indexOf(id));
			}
		}
		System.out.println("Mis IDs:");
		System.out.println(misIDs);

		if(misIDs!=null && misIDs.contains(id)){
			System.out.println("Se elimino correctamente - PersonaContoller - eliminarContenido");
			return interPersona.savePersona(personaUsuario);
		}else {
			System.out.println("Se detecto un problema de seguridad - PersonaContoller - eliminarContenido");
			return null;
		}
	}

	@GetMapping ("/instancia/{accion}")
	public Persona instanciaPersona(@PathVariable int accion){
		System.out.println("PersonaContoller - instanciaPersona");
				//Aparentemente algunos metodos afectan la entidad y la DB en simultaneo por con
		//la metodologia Lazy por lo que se observan asincronismos en la consola

		Persona personaVacia = new Persona();
		Persona personaDuenio;

		//Utilizo los metodos de java para obtener el username de la persona logueada
		Authentication autoridades=SecurityContextHolder.getContext().getAuthentication();
		Object principal = autoridades.getPrincipal();

		//Obtengo la entidad asociada a la sesion
		Long idPrincipal=obtenerId(principal.toString());
		Persona personaUsuario=interPersona.findPersona(idPrincipal);

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

			personaUsuario.setId(idPrincipal);
			BeanUtils.copyProperties(personaDuenio.getResumen(),personaUsuario.getResumen());
			personaUsuario.getResumen().setId(idPrincipal);

			//Desconecto las entidades de cada lista y agrego cada elemento a la entidad del usuario
			for(Educacion educacion : personaDuenio.getEducaciones()){
				this.entityManager.detach(educacion);
				educacion.setId(null);
				personaUsuario.getEducaciones().add(educacion);
			}
			for(Experiencia experiencia : personaDuenio.getExperiencias()){
				this.entityManager.detach(experiencia);
				experiencia.setId(null);
				personaUsuario.getExperiencias().add(experiencia);
			}
			for(Habilidad habilidad : personaDuenio.getHabilidades()){
				this.entityManager.detach(habilidad);
				habilidad.setId(null);
				personaUsuario.getHabilidades().add(habilidad);
			}
			for(Proyecto proyecto : personaDuenio.getProyectos()){
				Proyecto proyectoAux=new Proyecto();
				BeanUtils.copyProperties(proyecto,proyectoAux);
				proyectoAux.setId(null);
				/*Proyecto proyectoAux = new Proyecto(
						null,
						proyecto.getTitulo(),
						proyecto.getPeriodo(),
						proyecto.getDescripcion(),
						proyecto.getUrl(),
						proyecto.getFotos()
				);*/
				System.out.println("proyectoAux");
				System.out.println(proyectoAux);
				personaUsuario.getProyectos().add(proyectoAux);
			}
			for(Redes red : personaDuenio.getRedes()){
				this.entityManager.detach(red);
				red.setId(null);
				personaUsuario.getRedes().add(red);
			}

		}else {
			//interPersona.savePersona(new Persona(idPrincipal,"Lala","Lalaland",null,null,null,null,null,new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>()));

			System.out.println("Nuevo y vacio");
			//Creo una nueva entidad casi vacia
			personaUsuario=new Persona(
				idPrincipal,
		false,
				new Resumen(idPrincipal,"Nombre","Apellido",null,null,null,null,null,null,null,null),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>()
			);
		}
		return interPersona.savePersona(personaUsuario);
	}

	@GetMapping ("/togglepublico/{bool}")
	public String togglePublico(@PathVariable Boolean bool){
		System.out.println("PersonaContoller - togglePublico");

		String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
		Long id=obtenerId(username);
		Persona persona = interPersona.findPersona(id);
		persona.setPublico(bool);
		interPersona.savePersona(persona);
		return username;
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

	public String obtenerUsername(@PathVariable Long id){
		System.out.println("PersonaContoller - obtenerUsername");
		return interPersona.getUsernameById(id);
	}
	
	private Long obtenerId(String username){
		System.out.println("PersonaContoller - obtenerId");
		return interPersona.getIdByUsername(username);
	}

	//Solo para admins
	@GetMapping ("/traer")
	public List<Persona> getPersona(){
		System.out.println("PersonaContoller - getPersona");
		return interPersona.getPersona();
	}

	//Solo para admins
	@DeleteMapping ("/borrar/{id}")
	public String eliminarPersona(@PathVariable Long id){
		System.out.println("PersonaContoller - eliminarPersona");
		interPersona.deletePersona(id);
		return "Funado";
	}
}

