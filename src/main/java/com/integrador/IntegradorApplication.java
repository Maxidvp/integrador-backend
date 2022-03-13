package com.integrador;


import java.util.ArrayList;

import com.integrador.services.PersonasService;
import com.integrador.tablas.Personas;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.integrador.tablas.Role;
import com.integrador.tablas.Usuario;
import com.integrador.services.UsuarioService;

@SpringBootApplication
public class IntegradorApplication {
	public static void main(String[] args) {
		SpringApplication.run(IntegradorApplication.class, args);
	}
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
/*
	@Bean
	CommandLineRunner run(UsuarioService userService, PersonasService persoService) {
		return args -> {
			userService.saveRole(new Role(null,"ROLE_USER"));
			userService.saveRole(new Role(null,"ROLE_MANAGER"));
			userService.saveRole(new Role(null,"ROLE_ADMIN"));
			userService.saveRole(new Role(null,"ROLE_SUPER_ADMIN"));
			
			//userService.saveUser(new Usuario(null,"Walter White","walterw","1234",new ArrayList<>(),null));
			//userService.saveUser(new Usuario(null,"Nicola Tesla","tesla","1234",new ArrayList<>(),null));
			userService.saveUser(new Usuario(null,"maxidvp@gmail.com","Maxidvp","1234",new ArrayList<>(),
				persoService.savePersona(new Personas(null,"Cristian","Rocabado",null,null,null,null,null,new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>()))
			));


			//userService.addRoleToUser("walterw","ROLE_USER");
			//userService.addRoleToUser("tesla","ROLE_USER");
			//userService.addRoleToUser("tesla","ROLE_ADMIN");
			userService.addRoleToUser("maxidvp","ROLE_USER");
			userService.addRoleToUser("maxidvp","ROLE_MANAGER");
			userService.addRoleToUser("maxidvp","ROLE_ADMIN");
			userService.addRoleToUser("maxidvp","ROLE_SUPER_ADMIN");
		};
	}*/
}
