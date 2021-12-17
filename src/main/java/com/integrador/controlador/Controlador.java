package com.integrador.controlador;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Controlador {
	@RequestMapping("/barra")
	public String barra () {
		return "Hola vengo del controlador pero con barra";
	}
	@RequestMapping
	public String respuesta () {
		return "Hola vengo del controlador";
	}
}
