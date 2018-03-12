package com.Federico.ProyectoFinal.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.Federico.ProyectoFinal.Model.*;

@Controller
public class MainController {
	// DAOS
	@Autowired
	private DaoOrden daoOrden;
	
	@Autowired
	private DaoCliente daoCliente;
	
	@Autowired
	private DaoRepuesto daoRepuesto;
	
	@Autowired
	private DaoOrdenRepuesto daoOrdenRepuesto;
	
	@RequestMapping(value = "/",
					method = RequestMethod.GET)
	public String getIndex(){
		return "index";
	}
	
	@RequestMapping(value = "/cliente",
					method = RequestMethod.GET)
	public ModelAndView agregarPropietario(){
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.addObject("cliente", new Cliente());
		modelAndView.setViewName("formularioCliente");
		
		return modelAndView;
	}
	
	// Lista las ordenes 
	@RequestMapping(value = "/ordenes",
			method = RequestMethod.GET)
	public ModelAndView listarOrdenes(){
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.addObject("ordenes", daoOrden.findAll());
		modelAndView.setViewName("listadoOrdenesReparacion");
		
		return modelAndView;
	}
	
	// Muestra el formulario para dar de alta una orden
	@RequestMapping(value = "/orden",
			method = RequestMethod.GET)
	public ModelAndView agregarOrden(){
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.addObject("orden", new Orden());
		modelAndView.addObject("clientes", daoCliente.findAll());
		modelAndView.setViewName("formularioOrden");
		
		return modelAndView;
	}
	
	//Guarda la orden en la base de datos
	@RequestMapping(value = "/orden",
			method = RequestMethod.POST)
	public String agregarOrden(@ModelAttribute Orden orden){
		//TODO: Validaciones
		java.util.Date dt = new java.util.Date();

		orden.setFechaIngreso(dt);
		daoOrden.save(orden);
		
		return "redirect:/orden/repuestos?idOrden=" + orden.getIdOrden();
	}

	// Muestra un listado con los repuestos de la orden
	// Muestra un formulario para agregar un repuesto
	@RequestMapping(value = "/orden/repuestos",
			method = RequestMethod.GET)
	public ModelAndView listarRepuestos(@RequestParam("idOrden")Long idOrden){
		ModelAndView modelAndView = new ModelAndView();
		Orden orden = daoOrden.findOne(idOrden);
		
		if(orden == null){
			//TODO: mensaje error
		}
		
		// Nueva ordenRepuesto para el form 
		OrdenRepuesto ordenRepuesto = new OrdenRepuesto();
		ordenRepuesto.setOrden(orden);
		
		modelAndView.addObject("ordenRepuesto", ordenRepuesto);
		modelAndView.addObject("ordenRepuestos", daoOrdenRepuesto.findByOrden(orden));
		modelAndView.addObject("repuestos", daoRepuesto.findAll());
		modelAndView.setViewName("repuestosPorOrden");
		
		return modelAndView;
	}

	// Agrega un repuesto a la orden
	@RequestMapping(value = "/orden/repuestos",
			method = RequestMethod.POST)
	public String agregarRepuesto(@ModelAttribute OrdenRepuesto ordenRepuesto){
		//TODO: validar con try-catch?
		if(!daoOrden.exists(ordenRepuesto.getOrden().getIdOrden()) || 
			!daoRepuesto.exists(ordenRepuesto.getRepuesto().getIdRepuesto())){
			//TODO: mensaje error
		}
		
		//TODO: cambiar la cantidad de repuesto si el repuesto ya esta en la orden
		daoOrdenRepuesto.save(ordenRepuesto);
		
		return "redirect:/orden/repuestos?idOrden=" + ordenRepuesto.getOrden().getIdOrden();
	}
	
	// Muestra el formulario para agregar las horas trabajadas
	// y cerrar la orden de reparacion
	@RequestMapping(value = "/orden/cerrar",
			method = RequestMethod.GET)
	public ModelAndView cerrarOrden(@RequestParam("idOrden")Long idOrden){
		//TODO: validar con try-catch?
		ModelAndView modelAndView = new ModelAndView();
		Orden orden = daoOrden.findOne(idOrden);
		
		if(orden == null){
			//TODO: mensaje error
		}
		
		modelAndView.addObject("orden", orden);
		modelAndView.setViewName("ordenReparacion");
		
		return modelAndView;
	}

	// Cierra la orden si estan cargadas las horas trabajadas
	// Redirije a la factura
	@RequestMapping(value = "/orden/cerrar",
			method = RequestMethod.POST)
	public ModelAndView cerrarOrden(@ModelAttribute Orden ordenHtml){
		Orden orden = daoOrden.findOne(ordenHtml.getIdOrden());
		
		if(orden.getHorasTrabajadas() == null || orden == null){
			//TODO: mensaje error
		}
		
		orden.setHorasTrabajadas(ordenHtml.getHorasTrabajadas());
		orden.setCerrada(true);
		daoOrden.save(orden);
		
		return facturar(orden);
	}
	
	//Muestra el detalle de la orden
	private ModelAndView facturar(Orden orden){
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.addObject("orden", orden);
		modelAndView.setViewName("factura");
		
		return modelAndView;
	}
	
	
	// Agrega un propietario y redirige a la pantalla para agregar repuestos
	@RequestMapping(value = "/propietario",
					method = RequestMethod.POST)
	public ModelAndView agregarPropietario(@ModelAttribute Cliente cliente, @RequestParam("idOrden")Long idOrden ){
		daoCliente.save(cliente);
		
		return listarRepuestos(idOrden);
	}
	
}
