package com.integrador.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.integrador.tablas.Role;

public interface RoleRepository  extends JpaRepository<Role,Long>{
	Role findByName(String name);
}