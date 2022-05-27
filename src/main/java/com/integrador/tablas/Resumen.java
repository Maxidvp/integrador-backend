package com.integrador.tablas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Entity;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Resumen {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String apellido;
    private String titulo;
    private String direccion;
    private String telefono;
    private String email;
    private String nacimiento;
    @Column(length = 512)
    private String sobremi;
    private String foto;
    private String banner;
}
