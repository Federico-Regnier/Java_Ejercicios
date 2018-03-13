package com.Federico.ProyectoFinal.Model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Empleado {
	@Id
	@GeneratedValue
	private long idEmpleado;
	
	private String nombre, usuario, contrasenia;
	private Integer dni;
	
	@OneToMany(mappedBy = "empleado")
	private List<Orden> ordenes;
	
	public Empleado(){}
	
	public Empleado(Long idEmpleado){
		this.idEmpleado = idEmpleado;
	}

	public Empleado(String usuario, String contrasenia){
		this.usuario = usuario;
		this.contrasenia = contrasenia;
	}
	
	public long getIdEmpleado() {
		return idEmpleado;
	}

	public void setIdEmpleado(long idEmpleado) {
		this.idEmpleado = idEmpleado;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getContrasenia() {
		return contrasenia;
	}

	public void setContrasenia(String contrasenia) {
		this.contrasenia = contrasenia;
	}

	public List<Orden> getOrdenes() {
		return ordenes;
	}

	public void setOrdenes(List<Orden> ordenes) {
		this.ordenes = ordenes;
	}

	public Integer getDni() {
		return dni;
	}
	
	public void setDni(Integer dni) {
		this.dni = dni;
	}
}
