package com.Federico.ProyectoFinal.Model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DaoOrden extends CrudRepository<Orden, Long>{
	List<Orden> findAllByOrderByCerrada();
	List<Orden> findByEmpleadoOrderByCerradaAscFechaIngresoDesc(Empleado empleado);
}
