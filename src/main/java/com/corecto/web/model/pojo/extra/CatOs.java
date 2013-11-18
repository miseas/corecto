package com.corecto.web.model.pojo.extra;

// Generated 11-nov-2013 20:22:20 by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * CatOs generated by hbm2java
 */
public class CatOs implements java.io.Serializable {

	private int idos;
	private String nombre;
	private String descripcion;
	private Boolean estado;
	@JsonIgnore
	private Set<Paciente> pacientes = new HashSet<Paciente>(0);

	public CatOs() {
	}

	public CatOs(int idos) {
		this.idos = idos;
	}

	public CatOs(int idos, String nombre, String descripcion, Boolean estado,
			Set<Paciente> pacientes) {
		this.idos = idos;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.estado = estado;
		this.pacientes = pacientes;
	}

	public int getIdos() {
		return this.idos;
	}

	public void setIdos(int idos) {
		this.idos = idos;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Boolean getEstado() {
		return this.estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}

	public Set<Paciente> getPacientes() {
		return this.pacientes;
	}

	public void setPacientes(Set<Paciente> pacientes) {
		this.pacientes = pacientes;
	}

}