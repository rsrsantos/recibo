package com.AppRH.AppRH.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.AppRH.AppRH.models.Produto;
import com.AppRH.AppRH.models.Recibo;
import com.AppRH.AppRH.repository.ReciboRepository;

@Controller
public class ReciboController {
	
	@Autowired
	ReciboRepository reciboRepository;
//	
//	// GET que lista recibos
//		@RequestMapping("/recibos")
//		public ModelAndView listaRecibos() {
//			ModelAndView mv = new ModelAndView("recibo/lista-recibo");
//			Iterable<Recibo> recibos = reciboRepository.findAll();
//			mv.addObject("recibos", recibos);
//			return mv;
//		}
		
		@GetMapping("/recibos")
		public String showPage(Model model, @RequestParam(defaultValue = "0") int page) {
			model.addAttribute("recibos", reciboRepository.findAll(PageRequest.of(page, 5, Sort.by(Order.desc("id")))));
			model.addAttribute("currentPage", page);
			return "recibo/lista-recibo";
		}
		
		// GET que chama o form para cadastrar Recibos
		@RequestMapping("/cadastrarRecibo")
		public String form() {
			return "recibo/form-recibo";
		}
		
		// GET que deleta recibo
		@RequestMapping("/deletarRecibo")
		public String deletarRecibo(long id) {
			Recibo recibo = reciboRepository.findById(id);
			reciboRepository.delete(recibo);
			return "redirect:/recibos";
		}

}
