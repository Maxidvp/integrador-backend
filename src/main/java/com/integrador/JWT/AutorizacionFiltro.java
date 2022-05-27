package com.integrador.JWT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AutorizacionFiltro extends OncePerRequestFilter{

	@Autowired
	public CrearJWT crearJWT;

	public AutorizacionFiltro(CrearJWT crearJWT) {
		this.crearJWT = crearJWT;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		System.out.println("AutorizacionFiltro - doFilterInternal");
		if(request.getServletPath().equals("/usuario/login")
				|| request.getServletPath().equals("/usuario/token/refresh")
				|| request.getServletPath().equals("/usuario/usernamelibre/**")
				|| request.getServletPath().equals("/usuario/emaillibre/**")
				|| request.getServletPath().equals("/persona/publico/**")) {
			filterChain.doFilter(request, response);
		}else {
			String authorizationHeader=request.getHeader(HttpHeaders.AUTHORIZATION);
			if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
				try {//Aca si se esta autorizado
					System.out.println("AutorizacionFiltro - doFilterInternal - authorizationHeader try");
					String token = authorizationHeader.substring("Bearer ".length());
					/*Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
					JWTVerifier verifier = JWT.require(algorithm).build();
					DecodedJWT decodedJWT = verifier.verify(token);*/
					DecodedJWT decodedJWT = crearJWT.decodJWT(token);
					String username = decodedJWT.getSubject();
					String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
					Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
					stream(roles).forEach(role -> {
						authorities.add(new SimpleGrantedAuthority(role));
					});
					UsernamePasswordAuthenticationToken authenticationToken =
							new UsernamePasswordAuthenticationToken(username,null,authorities);
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
					filterChain.doFilter(request, response);
				}catch(Exception exception) {//Aca si NO se esta autorizado
					System.out.println("AutorizacionFiltro - doFilterInternal - authorizationHeader catch");
					log.error("Error de logeo en : {}", exception.getMessage());
					response.setHeader("error", exception.getMessage());
					//response.setStatus(HttpStatus.FORBIDDEN.value());
					//response.sendError(HttpStatus.FORBIDDEN.value());
					Map<String,String> error = new HashMap<>();
					error.put("error_message", exception.getMessage());
					response.setContentType(MediaType.APPLICATION_JSON_VALUE);
					new ObjectMapper().writeValue(response.getOutputStream(), error);
				}
			}else {
				System.out.println("Autorizacion filtro - 75 - Bypass por header null");
				filterChain.doFilter(request, response);
			}
		}
	}
}