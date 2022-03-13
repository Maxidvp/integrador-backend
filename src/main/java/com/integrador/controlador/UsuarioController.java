package com.integrador.controlador;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.integrador.services.IPersonasService;
import com.integrador.services.IUsuarioService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.integrador.tablas.Personas;
import com.integrador.tablas.Role;
import com.integrador.tablas.Usuario;

import lombok.Data;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")/*Revisar si se puede quitar en el servidor final*/
public class UsuarioController {
	private final IUsuarioService userService;
	
	@Autowired
	private IPersonasService interPersona;
	
	@GetMapping("/user/{username}")
	public Usuario traerUsername(@PathVariable String username){
		System.out.println("traer user");
		return userService.getUser(username);
	}
	
	@GetMapping("/users")
	public ResponseEntity<List<Usuario>>getUsers(){
		return ResponseEntity.ok().body(userService.getUsers());
	}
	
	/*@GetMapping("/usernamelibre/{username}")
	public ResponseEntity<Long> existeUsername(@PathVariable String username){
		System.out.println("em username libre");
		return ResponseEntity.ok().body(userService.existeUsername(username));
	}
	@GetMapping("/emaillibre/{email}")
	public ResponseEntity<Long> existeEmail(@PathVariable String email){
		System.out.println("en email libre");
		return ResponseEntity.ok().body(userService.existeEmail(email));
	}*/
	@GetMapping("/usernamelibre/{username}")
	public Long existeUsername(@PathVariable String username){
		System.out.println("em username libre");
		return userService.existeUsername(username);
	}
	@GetMapping("/emaillibre/{email}")
	public Long existeEmail(@PathVariable String email){
		System.out.println("en email libre");
		return userService.existeEmail(email);
	}
	
	@PostMapping("/user/save")
	public ResponseEntity<Usuario>saveUser(@RequestBody Usuario user){//
		System.out.println("UsuarioController 55");
		URI uri=URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
		System.out.println(uri);
		if(existeUsername(user.getUsername())!=0 || existeEmail(user.getEmail())!=0 ){
			System.out.println("Se detecto un problea de seguridad");
			return null;
		}
		Usuario usuario = userService.saveUser(user);
		System.out.println(usuario);
		userService.addRoleToUser(usuario.getUsername(),"ROLE_USER");
		System.out.println("despues del rol");
		
		Personas persona = new Personas();
		persona.setApellido("Apellido2");
		persona.setNombre("Nombre2");
		//System.out.println(interPersona.savePersona(persona));
		usuario.setPersona(interPersona.savePersona(persona));
		return ResponseEntity.created(uri).body(userService.getUser(user.getUsername()));
	}
	
	@PostMapping("/role/save")
	public ResponseEntity<Role>saveRole(@RequestBody Role role){
		URI uri=URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
		return ResponseEntity.created(uri).body(userService.saveRole(role));
	}
	
	@PostMapping("/role/addtouser")
	public ResponseEntity<?>addRoleToUser(@RequestBody RoleToUserForm form){
		userService.addRoleToUser(form.getUsername(),form.getRolename());
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/token/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws StreamWriteException, DatabindException, IOException{
		String authorizationHeader=request.getHeader(HttpHeaders.AUTHORIZATION);
		System.out.println("Antes del if");
		if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			try {
					System.out.println("Estoy en el try");
				String refresh_token = authorizationHeader.substring("Bearer ".length());
				Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build();
				DecodedJWT decodedJWT = verifier.verify(refresh_token);
				String username = decodedJWT.getSubject();
				Usuario user = userService.getUser(username);
				

				String access_token=JWT.create()
						.withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis()+10*60*1000))
						.withIssuer(request.getRequestURL().toString())
						.withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
						.sign(algorithm);

				Map<String, String> tokens=new HashMap<>();
				tokens.put("access_token", access_token);
				tokens.put("refresh_token", refresh_token);
				System.out.println("Usuariocontroller 96 - acces_token");
				System.out.println(access_token);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);
				
				
			}catch(Exception exception) {
					System.out.println("Error de logeo en");
				response.setHeader("error", exception.getMessage());
				response.setStatus(HttpStatus.FORBIDDEN.value());
				Map<String,String> error = new HashMap<>();
				error.put("error_message", exception.getMessage());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}
		}else {
			System.out.println("En el else");
			//throw new RuntimeException("No se encontro el refresh token");
		}
	}
	
	@GetMapping("/foto")
	public ResponseEntity<String> fotoPerfil(){
		System.out.println("enfoto de perfil");
		//userService.existeUsername(username)
		String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
		return ResponseEntity.ok().body(userService.traerFotobyUsername(username));
	}

}


@Data
class RoleToUserForm{
	private String username;
	private String rolename;
}


