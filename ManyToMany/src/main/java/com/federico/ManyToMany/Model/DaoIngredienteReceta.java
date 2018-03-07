package com.federico.ManyToMany.Model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface DaoIngredienteReceta extends CrudRepository<IngredienteReceta, Long>{
	List<IngredienteReceta> findByReceta(Receta receta);
}
