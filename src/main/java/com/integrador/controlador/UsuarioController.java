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

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.integrador.modelo.IUsuarioService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	
	@GetMapping("/users")
	public ResponseEntity<List<Usuario>>getUsers(){
		return ResponseEntity.ok().body(userService.getUsers());
	}
	
	@PostMapping("/user/save")
	public ResponseEntity<Usuario>saveUser(@RequestBody Usuario user){
		URI uri=URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/apì/user/save").toUriString());
		return ResponseEntity.created(uri).body(userService.saveUser(user));
	}
	
	@PostMapping("/role/save")
	public ResponseEntity<Role>saveRole(@RequestBody Role role){
		URI uri=URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/apì/role/save").toUriString());
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

}


@Data
class RoleToUserForm{
	private String username;
	private String rolename;
}


