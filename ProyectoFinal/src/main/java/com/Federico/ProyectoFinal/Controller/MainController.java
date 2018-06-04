package com.Federico.ProyectoFinal.Controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.Federico.ProyectoFinal.Model.*;

@Controller
@SessionAttributes({"usuario", "orden"})
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
	
	@Autowired
	private DaoEmpleado daoEmpleado;
	
	// INDEX
	@RequestMapping(value = "/",
					method = RequestMethod.GET)
	public ModelAndView getIndex(){
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.setViewName("index");
		
		return modelAndView;
	}
	
	// Lista las ordenes del empleado logueado
	@RequestMapping(value = "/ordenes",
			method = RequestMethod.GET)
	public ModelAndView listarOrdenes(@ModelAttribute("usuario")Long idUsuario){
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.addObject("ordenes", daoOrden.findByEmpleadoOrderByCerradaAscFechaIngresoDesc(new Empleado(idUsuario)));
		modelAndView.setViewName("listadoOrdenesReparacion");
		
		return modelAndView;
	}
	
	// Muestra el formulario para dar de alta una orden
	@RequestMapping(value = "/orden",
			method = RequestMethod.GET)
	public ModelAndView agregarOrden(@ModelAttribute Cliente propietario, 
									 @ModelAttribute("usuario")Long idUsuario, String mensaje){
		ModelAndView modelAndView = new ModelAndView();
		Orden orden = new Orden();
		
		// Si le paso un propietario lo seleccciono
		if(propietario != null){
			orden.setCliente(propietario);
		}
		
		// Para la vista
		if(mensaje == null){
			mensaje = "";
		}
		
		modelAndView.addObject("orden", orden);
		modelAndView.addObject("clientes", daoCliente.findAll());
		modelAndView.addObject("propietarios", daoCliente.findAll());
		modelAndView.addObject("mensaje", mensaje);
		modelAndView.setViewName("formularioOrden");
		
		return modelAndView;
	}
	
	//Guarda la orden en la base de datos
	@RequestMapping(value = "/orden",
			method = RequestMethod.POST)
	public ModelAndView agregarOrden(@ModelAttribute Orden orden, 
									 @ModelAttribute("usuario")Long idUsuario, 
									 Model model, HttpSession session){
		// Agrega la fecha de ingreso a la orden
		Date date = new Date();
		orden.setFechaIngreso(date);
		
		// Agrega el empleado a la orden
		orden.setEmpleado(new Empleado(idUsuario));
		
		// Busca al cliente
		Cliente propietario = daoCliente.findOne(orden.getCliente().getIdCliente());
		
		// Si no existe, guarda la orden sin Cliente y lleva al form de agregar cliente
		if(propietario != null){
			orden.setCliente(propietario);
			daoOrden.save(orden);
			
			return listarRepuestos(orden.getIdOrden(),idUsuario, model, session);
		} 
		
		return agregarOrden(null, idUsuario, "No se encuentra el propietario");
	}

	// Muestra un listado con los repuestos de la orden
	// Muestra un formulario para agregar un repuesto
	@RequestMapping(value = "/orden/repuestos",
			method = RequestMethod.GET)
	public ModelAndView listarRepuestos(@RequestParam("idOrden")Long idOrden, 
										@ModelAttribute("usuario")Long idUsuario,
										Model model, 
										HttpSession session){
		ModelAndView modelAndView = new ModelAndView();
		Orden orden;

		// Si la orden no esta en la session o no es la correcta la traigo de la db
		if(!model.containsAttribute("orden") || ((Orden)session.getAttribute("orden")).getIdOrden() != idOrden){
			orden = daoOrden.findOne(idOrden);
			if(orden.getOrdenRepuestos() == null){
				orden.setOrdenRepuestos(new ArrayList<>());
			}
			model.addAttribute("orden", orden);
		} else {
			// Guardo la orden de la sesion en la variable
			orden = (Orden)session.getAttribute("orden");
		}
		
		// Nueva ordenRepuesto para el form 
		OrdenRepuesto ordenRepuesto = new OrdenRepuesto();
		ordenRepuesto.setOrden(orden);
		
		modelAndView.addObject("ordenRepuesto", ordenRepuesto);
		modelAndView.addObject("repuestos", daoRepuesto.findAll());
		modelAndView.setViewName("repuestosPorOrden");
		
		return modelAndView;
	}

	// Agrega un repuesto a la orden
	@RequestMapping(value = "/orden/repuestos",
			method = RequestMethod.POST)
	public String agregarRepuesto(@ModelAttribute OrdenRepuesto ordenRepuestoHtml, 
								  @ModelAttribute("orden")Orden orden){
		Repuesto repuesto = daoRepuesto.findOne(ordenRepuestoHtml.getRepuesto().getIdRepuesto());
		if(orden == null || 
			repuesto == null){
			return "redirect:/ordenes";
		}
		
		// Redirijo a la lista de ordenes si ya esta cerrada
		if(orden.isCerrada()){
			return "redirect:/ordenes";
		}
		
		//Verfico si el repuesto ya esta en la orden
		OrdenRepuesto ordenRepuesto = daoOrdenRepuesto.findByOrdenAndRepuesto(ordenRepuestoHtml.getOrden(), ordenRepuestoHtml.getRepuesto());
		if(ordenRepuesto != null){
			// Si el repuesto ya esta, le sumo la cantidad de repuesto pasada
			ordenRepuesto.setCantidadRepuesto(ordenRepuesto.getCantidadRepuesto() + ordenRepuestoHtml.getCantidadRepuesto());
			
			// Cambia la cantidad de repuesto del carrito
			Iterator<OrdenRepuesto> it = orden.getOrdenRepuestos().iterator();
			while(it.hasNext()){
				OrdenRepuesto oRep = it.next();
				if(oRep.getIdOrdenRepuesto() == ordenRepuesto.getIdOrdenRepuesto()){
					oRep.setCantidadRepuesto(oRep.getCantidadRepuesto() + ordenRepuestoHtml.getCantidadRepuesto());
				}
			}
			
		} else {
			// Agrego el repuesto a la session
			ordenRepuesto = ordenRepuestoHtml;
			if(orden.getOrdenRepuestos() == null){
				orden.setOrdenRepuestos(new ArrayList<>());
			}
			orden.getOrdenRepuestos().add(ordenRepuesto);
		}
		
		ordenRepuesto.setRepuesto(repuesto);
		daoOrdenRepuesto.save(ordenRepuesto);
		
		return "redirect:/orden/repuestos?idOrden=" + ordenRepuestoHtml.getOrden().getIdOrden();
	}
	
	// Borra un repuesto de la orden
	@RequestMapping(value = "/orden/repuesto/borrar",
			method = RequestMethod.GET)
	public String borrarRepuesto(@RequestParam("idOrdenRepuesto")Long idOrdenRepuesto, 
								 @ModelAttribute("orden")Orden orden){
		
		// Verifico que exista la ordenRepuesto a borrar
		if(daoOrdenRepuesto.exists(idOrdenRepuesto)){
			daoOrdenRepuesto.delete(idOrdenRepuesto);
			
			// Borra el repuesto del carrito
			for(int i = 0; i < orden.getOrdenRepuestos().size();i++){
				if(orden.getOrdenRepuestos().get(i).getIdOrdenRepuesto() == idOrdenRepuesto){
					orden.getOrdenRepuestos().remove(i);
				}
			}
			
		} else{
			return "redirect:/ordenes";
		}
		
		return "redirect:/orden/repuestos?idOrden=" + orden.getIdOrden();
	}
	
	// Muestra el formulario para agregar las horas trabajadas
	// y cerrar la orden de reparacion
	@RequestMapping(value = "/orden/cerrar",
			method = RequestMethod.GET)
	public ModelAndView cerrarOrden(@RequestParam("idOrden")Long idOrden,
									@ModelAttribute("usuario")Long idUsuario){
		ModelAndView modelAndView = new ModelAndView();
		Orden orden = daoOrden.findOne(idOrden);
		
		if(orden == null){
			return listarOrdenes(idUsuario);
		}
		
		modelAndView.addObject("orden", orden);
		modelAndView.setViewName("ordenReparacion");
		
		return modelAndView;
	}

	// Cierra la orden si estan cargadas las horas trabajadas
	// Redirije a la factura
	@RequestMapping(value = "/orden/cerrar",
			method = RequestMethod.POST)
	public ModelAndView cerrarOrden(@ModelAttribute Orden ordenHtml, 
									@ModelAttribute("usuario")Long idUsuario){
		Orden orden = daoOrden.findOne(ordenHtml.getIdOrden());
		
		// Si no existe la orden o no estan las horas trabajadas
		// Vuelvo a la lista
		if(orden == null || ordenHtml.getHorasTrabajadas() == null){
			return listarOrdenes(idUsuario);
		}
		
		// Si la orden esta cerrada, vuelve al listado
		if(orden.isCerrada()){
			return listarOrdenes(idUsuario);
		}
		
		orden.setHorasTrabajadas(ordenHtml.getHorasTrabajadas());
		orden.setCerrada(true);
		daoOrden.save(orden);
		
		return facturar(orden, idUsuario);
	}
	
	// Muestra la factura de la orden
	@RequestMapping(value = "/orden/factura",
					method = RequestMethod.GET)
	public ModelAndView mostrarFactura(@RequestParam("idOrden")long idOrden, 
									   @ModelAttribute("usuario")Long idUsuario){
		return facturar(daoOrden.findOne(idOrden), idUsuario);
	}
	
	//Muestra el detalle de la orden
	private ModelAndView facturar(Orden orden, Long idEmpleado){
		ModelAndView modelAndView = new ModelAndView();
		Empleado empleado = daoEmpleado.findOne(idEmpleado);
		
		modelAndView.addObject("orden", orden);
		modelAndView.addObject("empleado", empleado);
		modelAndView.setViewName("factura");
		
		return modelAndView;
	}
	
	// Muestra form para agregar o modificar un propietario
	@RequestMapping(value = "/propietario",
					method = RequestMethod.GET)
	public ModelAndView agregarPropietario(@ModelAttribute("usuario")Long idUsuario, 
										   @RequestParam(name = "idCliente", required = false)Long idCliente){
		ModelAndView modelAndView = new ModelAndView();
		Cliente cliente;
		
		if(idCliente != null){
			cliente = daoCliente.findOne(idCliente);
		} else {
			cliente = new Cliente();
		}
		
		modelAndView.addObject("propietario", cliente);
		modelAndView.setViewName("formularioCliente");
		
		return modelAndView;
	}
	
	// Agrega un propietario y redirige a la pantalla para agregar orden
	@RequestMapping(value = "/propietario",
					method = RequestMethod.POST)
	public ModelAndView agregarPropietario(@ModelAttribute Cliente cliente,
										   @ModelAttribute("usuario")Long idUsuario){
		Boolean  clienteNuevo = true;
		
		// Si no se pasa el cliente vuelve al form
		if(cliente == null){
			return agregarPropietario(idUsuario, null);
		}
		
		// Verifico si es un cliente nuevo o no
		if(cliente.getIdCliente() != 0){
			clienteNuevo = false;
		}
		
		// Guardo el cliente
		daoCliente.save(cliente);
		
		return clienteNuevo ? agregarOrden(cliente, idUsuario, "") : listarPropietarios(idUsuario);
	}
	
	// Listado de propietarios(clientes)
	@RequestMapping(value = "/propietarios",
					method = RequestMethod.GET)
	public ModelAndView listarPropietarios(@ModelAttribute("usuario")Long idUsuario){
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.addObject("propietarios", daoCliente.findAll());
		modelAndView.setViewName("listadoPropietarios");
		
		return modelAndView;
	}
	
	// Listado de repuestos
	@RequestMapping(value = "/repuestos",
					method = RequestMethod.GET)
	public ModelAndView listarRepuestos(@ModelAttribute("usuario")Long idUsuario){
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.addObject("repuestos", daoRepuesto.findAll());
		modelAndView.setViewName("listadoRepuestos");
		
		return modelAndView;
	}
	
	// Formulario para agregar un empleado
	@RequestMapping(value = "/empleado",
					method = RequestMethod.GET)
	public ModelAndView agregarEmpleado(){
		ModelAndView modelAndView = new ModelAndView();
		Empleado empleado = new Empleado();
		
		modelAndView.addObject("empleado", empleado);
		modelAndView.setViewName("formularioEmpleado");
		
		return modelAndView;
	}
	
	// Agrega un empleado a la db y muestra el form de login
	@RequestMapping(value = "/empleado",
					method = RequestMethod.POST)
	public String agregarEmpleado(@ModelAttribute Empleado empleado){
		if(empleado == null){
			return "redirect:/empleado";
		}
		
		daoEmpleado.save(empleado);
		
		return "redirect:/login";
	}
	
	// Formulario de login
	@RequestMapping(value = "/login",
					method = RequestMethod.GET)
	public ModelAndView login(String mensaje){
		ModelAndView modelAndView = new ModelAndView();
		if(mensaje == null){
			mensaje = "";
		}
		
		modelAndView.addObject("empleado", new Empleado());
		modelAndView.addObject("mensaje", mensaje);
		modelAndView.setViewName("login");
		
		return modelAndView;
	}
	
	// Guarda la id del empleado en usuario si el user y la pass son correctas
	@RequestMapping(value = "/login",
					method = RequestMethod.POST)
	public ModelAndView login(@ModelAttribute Empleado empleadoHtml, Model model){
		Empleado empleado = daoEmpleado.findByUsuarioAndContrasenia(empleadoHtml.getUsuario(), empleadoHtml.getContrasenia());
		
		if(empleado != null){
			model.addAttribute("usuario", empleado.getIdEmpleado());
		} else {
			return login("Usuario o contraseña incorrectos");
		}
		
		return listarOrdenes(empleado.getIdEmpleado());
	}
	
	// Termina la sesion
	@RequestMapping(value = "/logout",
					method = RequestMethod.GET)
	public String logout(SessionStatus status){
		status.setComplete();
		return "redirect:/login";
	}
	
	@ExceptionHandler(HttpSessionRequiredException.class)
	public String errorUsuario(){
		return "redirect:/login";
	}
}
