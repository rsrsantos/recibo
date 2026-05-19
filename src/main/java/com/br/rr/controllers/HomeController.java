package com.br.rr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.br.rr.service.PessoaService;
import com.br.rr.service.ReciboService;

@Controller
public class HomeController {

	private final PessoaService pessoaService;
	private final ReciboService reciboService;

	public HomeController(PessoaService pessoaService, ReciboService reciboService) {
		this.pessoaService = pessoaService;
		this.reciboService = reciboService;
	}

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("totalClientes", pessoaService.contar());
		model.addAttribute("totalRecibos", reciboService.contar());
		model.addAttribute("valorTotalRecibos", reciboService.somaTotal());
		model.addAttribute("ultimosRecibos", reciboService.ultimos(5));
		return "index";
	}

}
