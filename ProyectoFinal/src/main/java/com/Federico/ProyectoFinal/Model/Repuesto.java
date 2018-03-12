package com.Federico.ProyectoFinal.Model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Repuesto {
	@Id
	@GeneratedValue
	private long idRepuesto;
	
	private String descripcion;	
	private Double costo;
	
	@OneToMany(mappedBy = "repuesto")
	private List<OrdenRepuesto> ordenRepuestos;
	
	public Repuesto(){}

	public long getIdRepuesto() {
		return idRepuesto;
	}

	public void setIdRepuesto(long idRepuesto) {
		this.idRepuesto = idRepuesto;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Double getCosto() {
		return costo;
	}

	public void setCosto(Double costo) {
		this.costo = costo;
	}

	public List<OrdenRepuesto> getOrdenRepuestos() {
		return ordenRepuestos;
	}

	public void setOrdenRepuestos(List<OrdenRepuesto> ordenRepuestos) {
		this.ordenRepuestos = ordenRepuestos;
	}
}
