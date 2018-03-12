package com.Federico.ProyectoFinal.Controller;

import java.util.ArrayList;
import java.util.Date;

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
		if(idUsuario == null){
			modelAndView.setViewName("index");
			return modelAndView;
		}
		
		modelAndView.addObject("ordenes", daoOrden.findByEmpleadoOrderByCerrada(new Empleado(idUsuario)));
		modelAndView.setViewName("listadoOrdenesReparacion");
		
		return modelAndView;
	}
	
	// Muestra el formulario para dar de alta una orden
	@RequestMapping(value = "/orden",
			method = RequestMethod.GET)
	public ModelAndView agregarOrden(@ModelAttribute Cliente propietario, 
									@ModelAttribute("usuario")Long idUsuario, 
									Model model){
		ModelAndView modelAndView = new ModelAndView();
		Orden orden = new Orden();
		
		if(propietario != null){
			orden.setCliente(propietario);
		} 
		
		modelAndView.addObject("orden", orden);
		modelAndView.addObject("clientes", daoCliente.findAll());
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
		Date date = new java.util.Date();
		orden.setFechaIngreso(date);
		
		// Agrega el empleado a la orden
		orden.setEmpleado(new Empleado(idUsuario));
		
		// Busca al cliente
		Cliente propietario = daoCliente.findByDni(orden.getCliente().getDni());
		
		// Si no existe, guarda la orden sin Cliente y lleva al form de agregar cliente
		if(propietario != null){
			orden.setCliente(propietario);
			daoOrden.save(orden);
			return listarRepuestos(orden.getIdOrden(),idUsuario, model, session);
		} 
		
		// Agrega el cliente y guarda la orden
		propietario = orden.getCliente();
		orden.setCliente(null);
		daoOrden.save(orden);
		
		return listarOrdenes(idUsuario);
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
	public String agregarRepuesto(@ModelAttribute OrdenRepuesto ordenRepuestoHtml, @ModelAttribute("orden")Orden orden){
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
		} else {
			ordenRepuesto = ordenRepuestoHtml;
		}
		
		ordenRepuesto.setRepuesto(repuesto);
		daoOrdenRepuesto.save(ordenRepuesto);
		
		// Agrego el repuesto a la session
		if(orden.getOrdenRepuestos() == null){
			orden.setOrdenRepuestos(new ArrayList<>());
		}
		orden.getOrdenRepuestos().add(ordenRepuesto);
		
		return "redirect:/orden/repuestos?idOrden=" + ordenRepuestoHtml.getOrden().getIdOrden();
	}
	
	@RequestMapping(value = "/orden/repuesto/borrar",
			method = RequestMethod.GET)
	public String borrarRepuesto(@RequestParam("idOrdenRepuesto")Long idOrdenRepuesto, @ModelAttribute("orden")Orden orden){
		
		// Verifico que exista la ordenRepuest a borrar
		if(daoOrdenRepuesto.exists(idOrdenRepuesto)){
			daoOrdenRepuesto.delete(idOrdenRepuesto);
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
	public ModelAndView cerrarOrden(@RequestParam("idOrden")Long idOrden){
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
	public ModelAndView cerrarOrden(@ModelAttribute Orden ordenHtml, @ModelAttribute("usuario")Long idUsuario){
		Orden orden = daoOrden.findOne(ordenHtml.getIdOrden());
		
		// Si no existe la orden 0 no estan las horas trabajadas
		// Vuelvo a la lista
		if(orden == null || ordenHtml.getHorasTrabajadas() == null){
			//TODO: mensaje error
			return getIndex();
		}
		
		// Si la orden esta cerrada, vuelve al listado
		if(orden.isCerrada()){
			return listarOrdenes(idUsuario);
		}
		
		orden.setHorasTrabajadas(ordenHtml.getHorasTrabajadas());
		orden.setCerrada(true);
		daoOrden.save(orden);
		
		return facturar(orden);
	}
	
	@RequestMapping(value = "/orden/factura",
					method = RequestMethod.GET)
	public ModelAndView mostrarFactura(@RequestParam("idOrden")long idOrden){
		return facturar(daoOrden.findOne(idOrden));
	}
	
	//Muestra el detalle de la orden
	private ModelAndView facturar(Orden orden){
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.addObject("orden", orden);
		modelAndView.setViewName("factura");
		
		return modelAndView;
	}
	
	// Muestra form para agregar un propietario
	public ModelAndView agregarPropietario(Long idOrden, Cliente cliente){
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.addObject("propietario", cliente);
		modelAndView.addObject("idOrden", idOrden);
		modelAndView.setViewName("formularioCliente");
		
		return modelAndView;
	}
	
	// Agrega un propietario y redirige a la pantalla para agregar orden
	@RequestMapping(value = "/propietario",
					method = RequestMethod.POST)
	public ModelAndView agregarPropietario(@ModelAttribute Cliente cliente, 
										   @RequestParam("idOrden")Long idOrden, 
										   Model model, HttpSession session,
										   @ModelAttribute("usuario")Long idUsuario){
		if(cliente == null){
			return agregarPropietario(idOrden, null);
		}
		// Guardo el cliente
		daoCliente.save(cliente);
		
		//Agrego el cliente a la orden
		Orden orden = daoOrden.findOne(idOrden);
		orden.setCliente(cliente);
		daoOrden.save(orden);
		
		return listarRepuestos(idOrden, idUsuario, model, session);
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
	
	@RequestMapping(value = "/login",
					method = RequestMethod.POST)
	public String login(@ModelAttribute Empleado empleadoHtml, Model model){
		Empleado empleado = daoEmpleado.findByUsuarioAndContrasenia(empleadoHtml.getUsuario(), empleadoHtml.getContrasenia());
		if(empleado != null){
			model.addAttribute("usuario", empleado.getIdEmpleado());
		} else {
			return "redirect:/login";
		}
		
		return "redirect:/orden";
	}
	
	@RequestMapping(value = "/logout",
					method = RequestMethod.GET)
	public String logout(SessionStatus status){
		status.setComplete();
		return "redirect:/login";
	}
	
	@ExceptionHandler(HttpSessionRequiredException.class)
	public ModelAndView errorUsuario(){
		return login("");
	}
	
}
