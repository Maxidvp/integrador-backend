package com.integrador.tablas;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Proyecto {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String titulo;
	private String periodo;
	private String descripcion;
	private String url;
	@ElementCollection
	private List<String> fotos= new ArrayList<>();
}
