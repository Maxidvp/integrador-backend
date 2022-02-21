package com.integrador.tablas;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Redes {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private Long red_id;
	private String username;
}
