package com.integrador.tablas;


import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.Data;


@Data
@Entity 
public class Personas {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String nombre;
	private String apellido;
	private String direccion;
	private String telefono;
	private String email;
	private int edad;
	private String src;
	@OneToMany(targetEntity = Educaciones.class ,cascade = CascadeType.ALL)//mappedBy = "persona"
	@JoinColumn(name = "fk_educaciones", referencedColumnName ="id")
    private List<Educaciones> educaciones;
	
	@OneToMany(targetEntity = Experiencias.class ,cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_experiencias", referencedColumnName ="id")
    private List<Experiencias> experiencias;
	
	@OneToMany(targetEntity = Habilidades.class ,cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_habilidades", referencedColumnName ="id")
    private List<Habilidades> habilidades;
	
	@OneToMany(targetEntity = Proyectos.class ,cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_proyectos", referencedColumnName ="id")
    private List<Proyectos> proyectos;
	/*public Persona() {}
	
	public Persona(Long id, String nombre, String apellido, int edad) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.apellido = apellido;
		this.edad = edad;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public int getEdad() {
		return edad;
	}
	public void setEdad(int edad) {
		this.edad = edad;
	}
	@Override
	public String toString() {
		return "Persona [id=" + id + ", nombre=" + nombre + ", apellido=" + apellido + ", edad=" + edad + "]";
	}*/

}