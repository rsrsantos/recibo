package com.AppRH.AppRH.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.AppRH.AppRH.models.Recibo;
import com.AppRH.AppRH.repository.ReciboRepository;

@Controller
public class ReciboController {
	
	@Autowired
	ReciboRepository reciboRepository;
	
	// GET que lista recibos
		@RequestMapping("/recibos")
		public ModelAndView listaRecibos() {
			ModelAndView mv = new ModelAndView("recibo/lista-recibo");
			Iterable<Recibo> recibos = reciboRepository.findAll();
			mv.addObject("recibos", recibos);
			return mv;
		}
		
		// GET que chama o form para cadastrar Recibos
		@RequestMapping("/cadastrarRecibo")
		public String form() {
			return "recibo/form-recibo";
		}

}
