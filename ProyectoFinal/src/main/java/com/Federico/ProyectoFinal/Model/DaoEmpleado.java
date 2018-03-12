package com.Federico.ProyectoFinal.Model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DaoEmpleado extends CrudRepository<Empleado, Long>{
	Empleado findByUsuarioAndContrasenia(String usuario, String contrasenia);
	Long findIdEmpleadoByUsuarioAndContrasenia(String usuario, String contrasenia);
}
