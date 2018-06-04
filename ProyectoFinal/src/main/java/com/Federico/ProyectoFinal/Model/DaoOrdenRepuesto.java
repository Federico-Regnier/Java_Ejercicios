package com.Federico.ProyectoFinal.Model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DaoOrdenRepuesto extends CrudRepository<OrdenRepuesto, Long>{
	List<OrdenRepuesto> findByOrden(Orden orden);
	OrdenRepuesto findByOrdenAndRepuesto(Orden orden, Repuesto repuesto);
}
