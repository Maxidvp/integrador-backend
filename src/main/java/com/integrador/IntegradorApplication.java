package com.integrador;


import com.integrador.services.PersonasService;
import com.integrador.services.UsuarioService;
import com.integrador.tablas.Persona;
import com.integrador.tablas.Resumen;
import com.integrador.tablas.Role;
import com.integrador.tablas.Usuario;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class IntegradorApplication {
	public static void main(String[] args) {
		SpringApplication.run(IntegradorApplication.class, args);
	}
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	

	@Bean
	CommandLineRunner run(UsuarioService userService, PersonasService persoService) {
		return args -> {
			if(userService.existeUsername("Maxidvp")==0){
				System.out.println("IntegradorApplication - Creando admin");

				String masterPass="1234";
				userService.saveRole(new Role(null,"ROLE_USER"));
				userService.saveRole(new Role(null,"ROLE_MANAGER"));
				userService.saveRole(new Role(null,"ROLE_ADMIN"));
				userService.saveRole(new Role(null,"ROLE_SUPER_ADMIN"));

				userService.saveUser(new Usuario(null,"maxidvp@gmail.com","Maxidvp",masterPass,new ArrayList<>(),
						persoService.savePersona(
								new Persona(
										null,
										false,
										new Resumen(null,"Cristian","Rocabado",null,null,null,null,null,null,null,null),
										new ArrayList<>(),
										new ArrayList<>(),
										new ArrayList<>(),
										new ArrayList<>(),
										new ArrayList<>()
								)
						)
				));
				userService.saveUser(new Usuario(null,"error@error.com","error",masterPass,new ArrayList<>(),
						persoService.savePersona(
								new Persona(
										null,
										false,
										new Resumen(null,"NN","NN",null,null,null,null,null,null,null,null),
										new ArrayList<>(),
										new ArrayList<>(),
										new ArrayList<>(),
										new ArrayList<>(),
										new ArrayList<>()
								)
						)
				));
				userService.saveUser(new Usuario(null,"miportfolio@miportfolio.com","miportfolio",masterPass,new ArrayList<>(),
						persoService.savePersona(
								new Persona(
										null,
										false,
										new Resumen(null,"NN","NN",null,null,null,null,null,null,null,null),
										new ArrayList<>(),
										new ArrayList<>(),
										new ArrayList<>(),
										new ArrayList<>(),
										new ArrayList<>()
								)
						)
				));
				userService.saveUser(new Usuario(null,"cargando@cargando.com","cargando",masterPass,new ArrayList<>(),
						persoService.savePersona(
								new Persona(
										null,
										false,
										new Resumen(null,"NN","NN",null,null,null,null,null,null,null,null),
										new ArrayList<>(),
										new ArrayList<>(),
										new ArrayList<>(),
										new ArrayList<>(),
										new ArrayList<>()
								)
						)
				));
				userService.saveUser(new Usuario(null,"anonymousUser@anonymousUser.com","anonymousUser",masterPass,new ArrayList<>(),
						persoService.savePersona(
								new Persona(
										null,
										false,
										new Resumen(null,"NN","NN",null,null,null,null,null,null,null,null),
										new ArrayList<>(),
										new ArrayList<>(),
										new ArrayList<>(),
										new ArrayList<>(),
										new ArrayList<>()
								)
						)
				));

				userService.addRoleToUser("Maxidvp","ROLE_USER");
				userService.addRoleToUser("Maxidvp","ROLE_MANAGER");
				userService.addRoleToUser("Maxidvp","ROLE_ADMIN");
				userService.addRoleToUser("Maxidvp","ROLE_SUPER_ADMIN");
			}else{
				System.out.println("IntegradorApplication - Admin ya creado");
			}
		};
	}
}
