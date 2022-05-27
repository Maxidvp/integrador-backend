package com.integrador.tablas;

import java.util.List;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Persona {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	private boolean publico;
	//targetEntity = Educaciones.class -> la tabla con los Many
	//cascade = CascadeType.ALL -> le hereda los metodos del jpa a la tabla
	//orphanRemoval = true -> Cuando se edita, los elementos que fueron eliminados de la entity son borrados de la DB
	//name = "fk_educaciones" -> Nombre de las foreigr keys
	//referencedColumnName ="id" -> atributo de personas que se va a bindear con la fk

	@OneToOne(targetEntity = Resumen.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	private Resumen resumen;

	@OneToMany(targetEntity = Educacion.class ,cascade = CascadeType.ALL ,fetch = FetchType.LAZY, orphanRemoval = true)//mappedBy = "persona"
	@JoinColumn(name = "fk_educaciones", referencedColumnName ="id")
    private List<Educacion> educaciones;
	
	@OneToMany(targetEntity = Experiencia.class ,cascade = CascadeType.ALL ,fetch = FetchType.LAZY, orphanRemoval = true)//
	@JoinColumn(name = "fk_experiencias", referencedColumnName ="id")
    private List<Experiencia> experiencias;
	
	@OneToMany(targetEntity = Habilidad.class ,cascade = CascadeType.ALL ,fetch = FetchType.LAZY, orphanRemoval = true)//
	@JoinColumn(name = "fk_habilidades", referencedColumnName ="id")
    private List<Habilidad> habilidades;
	
	@OneToMany(targetEntity = Proyecto.class ,cascade = CascadeType.ALL ,fetch = FetchType.LAZY, orphanRemoval = true)//
	@JoinColumn(name = "fk_proyectos", referencedColumnName ="id")
    private List<Proyecto> proyectos;
	
	@OneToMany(targetEntity = Redes.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)//
	@JoinColumn(name = "fk_redes", referencedColumnName ="id")
    private List<Redes> redes;

}