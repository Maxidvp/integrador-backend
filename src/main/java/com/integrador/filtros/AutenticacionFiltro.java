package com.integrador.filtros;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.integrador.repositories.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class AutenticacionFiltro extends UsernamePasswordAuthenticationFilter{
	
	private final AuthenticationManager authenticationManager;
	private final UsuarioRepository userRepo;

	public AutenticacionFiltro(AuthenticationManager authenticationManager, UsuarioRepository userRepo) {
		this.authenticationManager=authenticationManager;
		this.userRepo = userRepo;
	}

	//Como se solicita el usuario en 2 metodos se lo deja como atributo de la clase para su acceso privado
	private String usuario;
	private void usermail(String username){
		if( Pattern.compile("^(.+)@(\\S+)$").matcher(username).matches() ){
			username=this.userRepo.getUsernamebyEmail(username);//traerUsername(username);
			System.out.println("Autenticacion filtro - Es un email");
			System.out.println(username);
		}else{
			System.out.println("Autenticacion filtro - Es un username");
		}
		this.usuario=username;
		//return username;
	}
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		//Ejecuto la funcion que obtiene el username apartir del mail en caso de que corresponda
		usermail(request.getParameter("username"));
		String username=this.usuario;
		String password=request.getParameter("password");
		System.out.println("Autenticacion filtro - 42");
		log.info("Username is: {}", username);
		log.info("Password is: {}", password);
		UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(username, password);
		return authenticationManager.authenticate(authenticationToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws IOException, ServletException {
		System.out.println("Autorizacion filtro - 52");
		User user=(User)authentication.getPrincipal();
		Algorithm algorithm=Algorithm.HMAC256("secret".getBytes());
		String access_token=JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis()+10*60*1000))
				.withIssuer(request.getRequestURL().toString())
				.withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.sign(algorithm);
		String refresh_token=JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis()+2592000000L))//30*24*60*60*1000
				.withIssuer(request.getRequestURL().toString())
				.sign(algorithm);
		
		log.info("El access token es: {}", access_token);
		log.info("El refresh token es: {}", refresh_token);
		//Respuesta en el header
		//response.setHeader("access_token", access_token);
		//response.setHeader("refresh_token", refresh_token);
		//////////////////////////
		//Respuesta en el body
		Map<String, String> tokens=new HashMap<>();
		tokens.put("access_token", access_token);
		tokens.put("refresh_token", refresh_token);
		String username=this.usuario;
		log.info("El user del token es: {}", username);
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
