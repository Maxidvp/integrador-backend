package com.integrador.seguridad;

import java.util.List;

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

import com.integrador.filtros.AutenticacionFiltro;
import com.integrador.filtros.AutorizacionFiltro;

import lombok.RequiredArgsConstructor;

@Configuration @EnableWebSecurity @RequiredArgsConstructor
public class SeguridadConfig extends WebSecurityConfigurerAdapter{//Para sobreescribir el usuario a loguear
	private final UserDetailsService userDetailsService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UsuarioRepository userRepo;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
		System.out.println("SeguridadConfig - 32");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		System.out.println("Autorizacion filtro - 37");
		//Cambia la url en la que se realiza el logueo
		http.cors().configurationSource(request -> {
		      var cors = new CorsConfiguration();
		      cors.setAllowedOrigins(List.of("*"));//"http://localhost:4200", "http://192.168.0.5:4200"
		      cors.setAllowedMethods(List.of("*"));//"GET","POST", "PUT", "DELETE", "OPTIONS"
		      cors.setAllowedHeaders(List.of("*"));
		      return cors;
		    });
		
		AutenticacionFiltro customAuthenticationFilter=new AutenticacionFiltro(authenticationManagerBean(), userRepo);
		customAuthenticationFilter.setFilterProcessesUrl("/api/login");
		
		
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.authorizeRequests().antMatchers(
				"/api/login",
				"/api/user/save",
				"/api/user/**",
				"/api/usernamelibre/**",
				"/api/emaillibre/**",
				"/api/token/refresh",
				"/personas/buscar/1",
				"/personas/publico/**"
		).permitAll();
		http.authorizeRequests().antMatchers(
				"/api/users",
				"/personas/traer"
		).hasAnyAuthority("ROLE_ADMIN");
		http.authorizeRequests().antMatchers(
				"/api/foto",
				"/api/user/save/**",
				"/personas/buscar/**",
				"/personas/instancia/**",
				"/personas/togglepublico/**",
				"/personas/editar"
		).hasAnyAuthority("ROLE_USER");
		http.authorizeRequests().anyRequest().authenticated();
		//http.authorizeRequests().anyRequest().permitAll();//permite todo
		//Si no se cambia la url del login
		//http.addFilter(new CustomAuthenticationFilter(authenticationManagerBean()));
		//Si se cambia la url del login
		http.addFilter(customAuthenticationFilter);
		http.addFilterBefore(new AutorizacionFiltro(), UsernamePasswordAuthenticationFilter.class);

	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception{
		System.out.println("Autorizacion filtro - 69");
		return super.authenticationManagerBean();
	}
}
