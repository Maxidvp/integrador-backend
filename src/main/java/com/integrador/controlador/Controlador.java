package com.integrador.controlador;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Controlador {
	@GetMapping("/hola/{nombre}")
	public String barra (@PathVariable String nombre) {
		return "Hola "+nombre+" vengo del controlador";
	}
	@RequestMapping
	public String respuesta () {
		return "Hola vengo del controlador";
	}
}
