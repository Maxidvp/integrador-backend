package com.integrador.tablas;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


import lombok.Data;

@Data
@Entity
public class Educaciones {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String periodo;
	private String lugar;
	private String titulo;
	private String src;
	
	/*@ManyToOne
    @JoinColumn(name = "FK_EDUCACION", nullable = false, updatable = false)
    private Persona persona;*/
}
