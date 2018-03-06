package com.federico.ManyToMany.Model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Ingrediente {
	@Id
	@GeneratedValue
	private long idIngrediente;
	
	private String nombre;
	
	@OneToMany(mappedBy = "ingrediente")
	private List<IngredienteReceta> listaIngredienteReceta;
	
	public Ingrediente() {}

	public long getIdIngrediente() {
		return idIngrediente;
	}

	public void setIdIngrediente(long idIngrediente) {
		this.idIngrediente = idIngrediente;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<IngredienteReceta> getListaIngredienteReceta() {
		return listaIngredienteReceta;
	}

	public void setListaIngredienteReceta(List<IngredienteReceta> listaIngredienteReceta) {
		this.listaIngredienteReceta = listaIngredienteReceta;
	}
}
