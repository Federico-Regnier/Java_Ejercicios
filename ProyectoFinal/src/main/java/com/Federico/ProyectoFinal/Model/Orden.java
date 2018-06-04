package com.Federico.ProyectoFinal.Model;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Orden {
	@Id
	@GeneratedValue
	private long idOrden;
	
	private static final Double PRECIO_HORA_TRABAJO = 350.0;
	
	private String patente, marca, falla;
	private Integer horasTrabajadas;
	private Date fechaIngreso;
	private Boolean cerrada;
	
	@ManyToOne
	@JoinColumn(name = "idCliente")
	private Cliente cliente;
	
	@OneToMany(mappedBy = "orden", 
			   fetch = FetchType.EAGER)
	private List<OrdenRepuesto> ordenRepuestos;
	
	@ManyToOne
	@JoinColumn(name = "idEmpleado")
	private Empleado empleado;
	
	public Orden(){
		this.cerrada = false;
	}

	public long getIdOrden() {
		return idOrden;
	}

	public void setIdOrden(long idOrden) {
		this.idOrden = idOrden;
	}

	public String getPatente() {
		return patente;
	}

	public void setPatente(String patente) {
		this.patente = patente;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getFalla() {
		return falla;
	}

	public void setFalla(String falla) {
		this.falla = falla;
	}

	public Integer getHorasTrabajadas() {
		return horasTrabajadas;
	}

	public void setHorasTrabajadas(Integer horasTrabajadas) {
		this.horasTrabajadas = horasTrabajadas;
	}

	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}
	
	public Cliente getCliente() {
		return cliente;
	}


	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}


	public static Double getPrecioHoraTrabajo() {
		return PRECIO_HORA_TRABAJO;
	}


	public List<OrdenRepuesto> getOrdenRepuestos() {
		return ordenRepuestos;
	}

	public void setOrdenRepuestos(List<OrdenRepuesto> ordenRepuestos) {
		this.ordenRepuestos = ordenRepuestos;
	}
	
	public Boolean isCerrada() {
		return cerrada;
	}
	
	public void setCerrada(Boolean cerrada) {
		this.cerrada = cerrada;
	}
	
	public Empleado getEmpleado() {
		return empleado;
	}
	
	
	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}
	
	public Double calcularCostoTotal(){
		return calcularCostoManoDeObra() + calcularCostoRepuestos();
	}
	
	public Double calcularCostoManoDeObra() {
		return horasTrabajadas * PRECIO_HORA_TRABAJO;
	}
	
	public Double calcularCostoRepuestos(){
		Double costoRepuestos = 0.0;
		
		for(OrdenRepuesto ordenRepuesto : this.ordenRepuestos){
			costoRepuestos += ordenRepuesto.getCostoTotal();
		}
		
		return costoRepuestos;
	}
}
