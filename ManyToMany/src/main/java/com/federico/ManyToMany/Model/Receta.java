package com.federico.ManyToMany.Model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Receta {
	@Id
	@GeneratedValue
	private long idReceta;
	
	private String name, descripcion;
	
	@OneToMany(mappedBy = "receta", 
			   fetch = FetchType.EAGER)
	private List<IngredienteReceta> listaIngredienteReceta;
	
	public Receta() {}

	public long getIdReceta() {
		return idReceta;
	}

	public void setIdReceta(long idReceta) {
		this.idReceta = idReceta;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public List<IngredienteReceta> getListaIngredienteReceta() {
		return listaIngredienteReceta;
	}

	public void setListaIngredienteReceta(List<IngredienteReceta> listaIngredienteReceta) {
		this.listaIngredienteReceta = listaIngredienteReceta;
	}
}
