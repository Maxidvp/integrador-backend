package com.integrador.JWT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integrador.repositories.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
public class AutenticacionFiltro extends UsernamePasswordAuthenticationFilter{
	
	private final AuthenticationManager authenticationManager;
	private final UsuarioRepository userRepo;
	private final CrearJWT crearJWT;

	public AutenticacionFiltro(AuthenticationManager authenticationManager, UsuarioRepository userRepo, CrearJWT crearJWT) {
		System.out.println("AutenticacionFiltro - AutenticacionFiltro");
		this.authenticationManager=authenticationManager;
		this.userRepo = userRepo;
		this.crearJWT = crearJWT;
	}

	//Como se solicita el usuario en 2 metodos se lo deja como atributo de la clase para su acceso privado
	private String usuario;
	private void usermail(String username){
		if( Pattern.compile("^(.+)@(\\S+)$").matcher(username).matches() ){
			username=this.userRepo.getUsernamebyEmail(username);//traerUsername(username);
			System.out.println("AutenticacionFiltro - usermail - Es un email");
		}else{
			System.out.println("AutenticacionFiltro - usermail - Es un username");
		}
		this.usuario=username;
	}
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		System.out.println("AutenticacionFiltro - attemptAuthentication");
		//Ejecuto la funcion que obtiene el username apartir del mail en caso de que corresponda
		usermail(request.getParameter("username"));
		String username=this.usuario;
		String password=request.getParameter("password");
		log.info("AutenticacionFiltro - attemptAuthentication - Username is: {}", username);
		log.info("AutenticacionFiltro - attemptAuthentication - Password is: {}", password);
		UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(username, password);
		return authenticationManager.authenticate(authenticationToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws IOException, ServletException {
		System.out.println("AutenticacionFiltro - successfulAuthentication");

		String access_token=crearJWT.crearAccessTokenPrincipal(request, authentication);
		String refresh_token=crearJWT.crearRefreshToken(request, authentication);
		
		log.info("AutenticacionFiltro - successfulAuthentication - access_token: {}", access_token);
		log.info("AutenticacionFiltro - successfulAuthentication - refresh_token: {}", refresh_token);
		//Respuesta en el header
		//response.setHeader("access_token", access_token);
		//response.setHeader("refresh_token", refresh_token);
		//////////////////////////
		//Respuesta en el body
		Map<String, String> tokens=new HashMap<>();
		tokens.put("access_token", access_token);
		tokens.put("refresh_token", refresh_token);
		String username=this.usuario;
		log.info("AutenticacionFiltro - successfulAuthentication - El user del token es: {}", username);
		tokens.put("username", username);
		
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		new ObjectMapper().writeValue(response.getOutputStream(), tokens);
	}
/*
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		// TODO Auto-generated method stub
		super.unsuccessfulAuthentication(request, response, failed);
	}
	*/
}
