package com.federico.ManyToMany.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class IngredienteReceta {
	@Id
	@GeneratedValue
	private long idIgredienteReceta;
	
	@ManyToOne
	@JoinColumn(name = "idReceta")
	private Receta receta;
	
	@ManyToOne
	@JoinColumn(name = "idIngrediente")
	private Ingrediente ingrediente;
	
	private Integer cantidadIngrediente;
	
	public IngredienteReceta() {}

	public long getIdIgredienteReceta() {
		return idIgredienteReceta;
	}

	public void setIdIgredienteReceta(long idIgredienteReceta) {
		this.idIgredienteReceta = idIgredienteReceta;
	}

	public Receta getReceta() {
		return receta;
	}

	public void setReceta(Receta receta) {
		this.receta = receta;
	}

	public Ingrediente getIngrediente() {
		return ingrediente;
	}

	public void setIngrediente(Ingrediente ingrediente) {
		this.ingrediente = ingrediente;
	}

	public Integer getCantidadIngrediente() {
		return cantidadIngrediente;
	}

	public void setCantidadIngrediente(Integer cantidadIngrediente) {
		this.cantidadIngrediente = cantidadIngrediente;
	}
}
