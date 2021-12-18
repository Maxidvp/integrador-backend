package com.integrador.controlador;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Controlador {
	@GetMapping("/hola/{nombre}")
	public String barra (@PathVariable String nombre) {
		return "Hola "+nombre+" vengo del controlador";
	}
	
	@PostMapping("/posteo")
	public String posteo(@RequestBody Ejemplo ejemplo) {
		return ejemplo.toString();
	}
	
	
	@RequestMapping
	public String respuesta () {
		return "Hola vengo del controlador";
	}
}
