package com.integrador.controlador;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.integrador.JWT.CrearJWT;
import com.integrador.tablas.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.integrador.services.IPersonasService;
import com.integrador.services.IUsuarioService;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {
	private final IUsuarioService userService;
	
	@Autowired
	private IPersonasService interPersona;

	@Autowired
	private CrearJWT crearJWT;

	//Solo para admin
	@GetMapping("/users")
	public ResponseEntity<List<Usuario>>getUsers(){
		return ResponseEntity.ok().body(userService.getUsers());
	}

	@GetMapping("/user/{username}")
	public Usuario traerUsername(@PathVariable String username){
		System.out.println("usuarioController - traerUsername");
		return userService.getUser(username);
	}

	@GetMapping("/usernamelibre/{username}")
	public Long existeUsername(@PathVariable String username){
		System.out.println("usuarioController - existeUsername");
		return userService.existeUsername(username);
	}

	@GetMapping("/emaillibre/{email}")
	public Long existeEmail(@PathVariable String email){
		System.out.println("usuarioController - existeEmail");
		return userService.existeEmail(email);
	}
	
	@PostMapping("/user/save")
	public ResponseEntity<Usuario>saveUser(@RequestBody Usuario user){//
		System.out.println("usuarioController - saveUser");
		URI uri=URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
		if(existeUsername(user.getUsername()) != 0 || existeEmail(user.getEmail()) != 0 ){
			System.out.println("Se detecto un problea de seguridad - usuarioController - saveUser 84");
			return null;
		}

		//Verifica el formato
		if( !( Pattern.compile("^[a-zA-Z0-9_-]{4,20}$").matcher(user.getUsername()).matches()
				&& Pattern.compile("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$").matcher(user.getEmail()).matches()
				&& Pattern.compile("^[a-zA-Z0-9_\\-!@#$%^&*]{4,128}$").matcher(user.getPassword()).matches() )){
			System.out.println("Se detecto un problea de seguridad - usuarioController - saveUser 92");
			return null;
		}

		Usuario usuario = userService.saveUser(user);
		System.out.println(usuario);
		userService.addRoleToUser(usuario.getUsername(),"ROLE_USER");
		
		Persona persona = new Persona(
			null,
			false,
			new Resumen(null,"Nombre","Apellido",null,null,null,null,null,null,null,null),
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>()
		);


		usuario.setPersona(interPersona.savePersona(persona));
		return ResponseEntity.created(uri).body(userService.getUser(user.getUsername()));
	}
	
	@PostMapping("/role/save")
	public ResponseEntity<Role>saveRole(@RequestBody Role role){
		System.out.println("usuarioController - saveRole");
		URI uri=URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
		return ResponseEntity.created(uri).body(userService.saveRole(role));
	}
	
	@PostMapping("/role/addtouser")
	public ResponseEntity<?>addRoleToUser(@RequestBody RoleToUserForm form){
		System.out.println("usuarioController - addRoleToUser");
		userService.addRoleToUser(form.getUsername(),form.getRolename());
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/token/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws StreamWriteException, DatabindException, IOException{
		System.out.println("usuarioController - refreshToken");
		String authorizationHeader=request.getHeader(HttpHeaders.AUTHORIZATION);
		if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			try {
				System.out.println("usuarioController - refreshToken - authorizationHeader - try");
				String refresh_token = authorizationHeader.substring("Bearer ".length());
				String access_token=crearJWT.crearAccessTokenUser(refresh_token,request, authorizationHeader);
				Map<String, String> tokens=new HashMap<>();
				tokens.put("access_token", access_token);
				tokens.put("refresh_token", refresh_token);
				System.out.println("usuarioController - refreshToken - access_token");
				System.out.println(access_token);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);
				
				
			}catch(Exception exception) {
				System.out.println("usuarioController - refreshToken - authorizationHeader - catch : Error de logueo");
				response.setHeader("error", exception.getMessage());
				response.setStatus(HttpStatus.FORBIDDEN.value());
				Map<String,String> error = new HashMap<>();
				error.put("error_message", exception.getMessage());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}
		}else {
			System.out.println("usuarioController - refreshToken - SIN authorizationHeader");
			//throw new RuntimeException("No se encontro el refresh token");
		}
	}
	
	@GetMapping("/foto")
	public ResponseEntity<String> fotoPerfil(){
		System.out.println("usuarioController - fotoPerfil");
		String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
		return ResponseEntity.ok().body(userService.traerFotobyUsername(username));
	}
}




