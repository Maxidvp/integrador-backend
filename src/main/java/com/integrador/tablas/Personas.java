package com.integrador.tablas;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Personas {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String nombre;
	private String apellido;
	private String titulo;
	private String direccion;
	private String telefono;
	private String email;
	private String nacimiento;
	private String sobremi;
	private String foto;
	private String banner;
	//targetEntity = Educaciones.class -> la tabla con los Many
	//cascade = CascadeType.ALL -> le hereda los metodos del jpa a la tabla
	//orphanRemoval = true -> Cuando se edita, los elementos que fueron eliminados son borrados de la DB
	//name = "fk_educaciones" -> Nombre de las foreigr keys
	//referencedColumnName ="id" -> atributo de personas que se va a bindear con la fk
	@OneToMany(targetEntity = Educaciones.class ,cascade = CascadeType.ALL)//mappedBy = "persona",orphanRemoval = true
	@JoinColumn(name = "fk_educaciones", referencedColumnName ="id")
    private List<Educaciones> educaciones;
	
	@OneToMany(targetEntity = Experiencias.class ,cascade = CascadeType.ALL)//,orphanRemoval = true
	@JoinColumn(name = "fk_experiencias", referencedColumnName ="id")
    private List<Experiencias> experiencias;
	
	@OneToMany(targetEntity = Habilidades.class ,cascade = CascadeType.ALL)//,orphanRemoval = true
	@JoinColumn(name = "fk_habilidades", referencedColumnName ="id")
    private List<Habilidades> habilidades;
	
	@OneToMany(targetEntity = Proyectos.class ,cascade = CascadeType.ALL)//,orphanRemoval = true
	@JoinColumn(name = "fk_proyectos", referencedColumnName ="id")
    private List<Proyectos> proyectos;
	
	@OneToMany(targetEntity = Redes.class ,cascade = CascadeType.ALL)//,orphanRemoval = true
	@JoinColumn(name = "fk_redes", referencedColumnName ="id")
    private List<Redes> redes;

}