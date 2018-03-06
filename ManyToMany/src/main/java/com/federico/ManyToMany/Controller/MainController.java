package com.federico.ManyToMany.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.federico.ManyToMany.Model.*;

@Controller
public class MainController {
	@Autowired
	private DaoIngrediente daoIngrediente;
	
	@Autowired
	private DaoReceta daoReceta;
	
	@Autowired
	private DaoIngredienteReceta daoIngredienteReceta;
	
	@RequestMapping(value = "/", 
					method = RequestMethod.GET)
	public String index() {
		return "index";
	}
	
	@RequestMapping(value = "/ingrediente", 
					method = RequestMethod.GET)
	public ModelAndView formularioIngrediente() {
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.addObject("ingrediente", new Ingrediente());
		modelAndView.setViewName("altaIngrediente");
		
		return modelAndView;
	}
	
	@RequestMapping(value = "/ingrediente", 
					method = RequestMethod.POST)
	public String altaIngrediente(@ModelAttribute Ingrediente ingrediente){
		daoIngrediente.save(ingrediente);
		
		return "redirect:/ingredientes";
	}
	
	@RequestMapping(value = "/ingredientes", 
					method = RequestMethod.GET)
	public ModelAndView listarIngredientes() {
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.addObject("ingredientes", daoIngrediente.findAll());
		modelAndView.setViewName("listadoIngredientes");
		
		return modelAndView;
	}
	
	@RequestMapping(value = "/receta", 
			method = RequestMethod.GET)
	public ModelAndView formularioReceta() {
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.addObject("receta", new Receta());
		modelAndView.addObject("ingredientes", daoIngrediente.findAll());
		modelAndView.setViewName("altaReceta");
		
		return modelAndView;
	}
	
	@RequestMapping(value = "/receta", 
					method = RequestMethod.POST)
	public String agregarReceta(@ModelAttribute Receta receta) {
		daoReceta.save(receta);
		
		return "redirect:/recetas";
	}
	
	@RequestMapping(value = "/recetas", 
					method = RequestMethod.GET)
	public ModelAndView listarRecetas() {
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.addObject("recetas", daoReceta.findAll());
		modelAndView.setViewName("listadoRecetas");
		
		return modelAndView;
	}
	
	@RequestMapping(value = "/receta/ingrediente", 
			method = RequestMethod.GET)
	public ModelAndView formularioIngredienteReceta(@RequestParam("idReceta")Long idReceta) {
	ModelAndView modelAndView = new ModelAndView();
	
		modelAndView.addObject("ingredientes", daoIngrediente.findAll());
		modelAndView.addObject("receta", daoReceta.findOne(idReceta));
		modelAndView.setViewName("agregarIngredienteEnReceta");
		
		return modelAndView;
	}
	
	@RequestMapping(value = "/receta/ingrediente", 
			method = RequestMethod.POST)
	public String agregarIngredienteReceta(@ModelAttribute Receta recetaHtml, @RequestParam("idIngrediente")Long idIngrediente, @RequestParam("cantidad") Integer cantidad) {
		Receta receta = daoReceta.findOne(recetaHtml.getIdReceta());
		IngredienteReceta ingredienteReceta = new IngredienteReceta();
		
		ingredienteReceta.setIngrediente(daoIngrediente.findOne(idIngrediente));
		ingredienteReceta.setReceta(receta);
		ingredienteReceta.setCantidadIngrediente(cantidad);
		daoIngredienteReceta.save(ingredienteReceta);
		
		return "redirect:/recetas";
	}
}
