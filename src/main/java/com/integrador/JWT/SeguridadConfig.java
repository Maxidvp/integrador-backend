package com.integrador.JWT;

import java.util.List;

import com.integrador.JWT.CrearJWT;
import com.integrador.repositories.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.integrador.JWT.AutenticacionFiltro;
import com.integrador.JWT.AutorizacionFiltro;

import lombok.RequiredArgsConstructor;

@Configuration @EnableWebSecurity @RequiredArgsConstructor
public class SeguridadConfig extends WebSecurityConfigurerAdapter{//Para sobreescribir el usuario a loguear
	private final UserDetailsService userDetailsService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UsuarioRepository userRepo;
	private final CrearJWT crearJWT;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		System.out.println("SeguridadConfig - configure auth");
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		System.out.println("SeguridadConfig - configure http");
		//Cambia la url en la que se realiza el logueo
		http.cors().configurationSource(request -> {
		      var cors = new CorsConfiguration();
		      cors.setAllowedOrigins(List.of("*"));//"http://localhost:4200", "http://192.168.0.5:4200", ""
		      cors.setAllowedMethods(List.of("*"));//"GET","POST", "PUT", "DELETE", "OPTIONS"
		      cors.setAllowedHeaders(List.of("*"));
		      return cors;
		    });
		
		AutenticacionFiltro customAuthenticationFilter=new AutenticacionFiltro(authenticationManagerBean(), userRepo, crearJWT);
		customAuthenticationFilter.setFilterProcessesUrl("/usuario/login");
		
		
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.authorizeRequests().antMatchers(
				"/usuario/login",
				"/usuario/user/save",
				"/usuario/user/**",
				"/usuario/usernamelibre/**",
				"/usuario/emaillibre/**",
				"/usuario/token/refresh",
				"/persona/buscar/1",
				"/persona/publico/**"
		).permitAll();
		http.authorizeRequests().antMatchers(
				"/usuario/users",
				"/persona/traer",
				"/persona/borrar/**"
		).hasAnyAuthority("ROLE_ADMIN");
		http.authorizeRequests().antMatchers(
				"/usuario/foto",
				"/usuario/user/save/**",
				"/persona/buscar/**",
				"/persona/instancia/**",
				"/persona/togglepublico/**",
				"/persona/agregar/**",
				"/persona/editar/**",
				"/persona/eliminar/**"
		).hasAnyAuthority("ROLE_USER");
		http.authorizeRequests().anyRequest().authenticated();
		//http.authorizeRequests().anyRequest().permitAll();//permite todo
		//Si no se cambia la url del login
		//http.addFilter(new CustomAuthenticationFilter(authenticationManagerBean()));
		//Si se cambia la url del login
		http.addFilter(customAuthenticationFilter);
		http.addFilterBefore(new AutorizacionFiltro(crearJWT), UsernamePasswordAuthenticationFilter.class);

	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception{
		System.out.println("SeguridadConfig - authenticationManagerBean");
		return super.authenticationManagerBean();
	}
}
