package com.br.rr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.br.rr.service.PlanoService;

@Controller
@RequestMapping("/inicio")
public class LandingController {

	private final PlanoService planoService;

	public LandingController(PlanoService planoService) {
		this.planoService = planoService;
	}

	@GetMapping
	public String landing(Model model) {
		model.addAttribute("planos", planoService.listarAtivos());
		return "landing";
	}

}
