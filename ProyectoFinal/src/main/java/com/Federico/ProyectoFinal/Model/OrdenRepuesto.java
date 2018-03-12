package com.Federico.ProyectoFinal.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class OrdenRepuesto {
	@Id
	@GeneratedValue
	private long idOrdenRepuesto;

	@ManyToOne
	@JoinColumn(name = "idRepuesto")
	private Repuesto repuesto;
	
	@ManyToOne
	@JoinColumn(name = "idOrden")
	private Orden orden;
	
	private Integer cantidadRepuesto;
	
	public OrdenRepuesto(){}
	
	public long getIdOrdenRepuesto() {
		return idOrdenRepuesto;
	}

	public void setIdOrdenRepuesto(long idOrdenRepuesto) {
		this.idOrdenRepuesto = idOrdenRepuesto;
	}

	public Repuesto getRepuesto() {
		return repuesto;
	}

	public void setRepuesto(Repuesto repuesto) {
		this.repuesto = repuesto;
	}

	public Orden getOrden() {
		return orden;
	}

	public void setOrden(Orden orden) {
		this.orden = orden;
	}

	public Integer getCantidadRepuesto() {
		return cantidadRepuesto;
	}
	
	public void setCantidadRepuesto(Integer cantidadRepuesto) {
		this.cantidadRepuesto = cantidadRepuesto;
	}
	
	// Devuelve el costo total de los repuestos
	public Double getCostoTotal(){
		return repuesto.getCosto() * cantidadRepuesto;
	}
}
