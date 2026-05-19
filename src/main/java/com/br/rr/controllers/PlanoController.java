package com.br.rr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.br.rr.exception.RecursoNaoEncontradoException;
import com.br.rr.models.Plano;
import com.br.rr.service.ContaService;
import com.br.rr.service.PlanoService;
import com.br.rr.service.UsuarioPlanoService;

@Controller
@RequestMapping("/planos")
public class PlanoController {

	private final PlanoService planoService;
	private final UsuarioPlanoService usuarioPlanoService;
	private final ContaService contaService;

	public PlanoController(PlanoService planoService, UsuarioPlanoService usuarioPlanoService,
			ContaService contaService) {
		this.planoService = planoService;
		this.usuarioPlanoService = usuarioPlanoService;
		this.contaService = contaService;
	}

	@GetMapping("/ativar")
	public String confirmar(@RequestParam Long plano, Model model) {
		try {
			Plano p = planoService.buscarPorId(plano);
			model.addAttribute("plano", p);
			return "planos/ativar";
		} catch (RecursoNaoEncontradoException ex) {
			return "redirect:/";
		}
	}

	@PostMapping("/ativar")
	public String ativar(@RequestParam Long plano, RedirectAttributes attrs) {
		usuarioPlanoService.ativar(contaService.usuarioLogado(), plano);
		attrs.addFlashAttribute("success", "Plano ativado com sucesso! Bom trabalho.");
		return "redirect:/";
	}

}
