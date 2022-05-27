package com.integrador.tablas;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Usuario {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String email;
	private String username;
	private String password;

	@ManyToMany(fetch = FetchType.EAGER)
	private Collection<Role> roles = new ArrayList<>();
	
	@OneToOne(fetch = FetchType.EAGER)
	@PrimaryKeyJoinColumn
	private Persona persona;
}
