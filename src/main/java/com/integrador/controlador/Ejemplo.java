package com.integrador.controlador;


public class Ejemplo {
	private int numero;
	private String nombre;
	private String profesion;
	
	public Ejemplo(int numero, String nombre, String profesion) {
		super();
		this.numero = numero;
		this.nombre = nombre;
		this.profesion = profesion;
	}
	
	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		this.numero = numero;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getProfesion() {
		return profesion;
	}
	public void setProfesion(String profesion) {
		this.profesion = profesion;
	}
	@Override
	public String toString() {
		return "Ejemplo [numero=" + numero + ", nombre=" + nombre + ", profesion=" + profesion + "]";
	}
	
}
