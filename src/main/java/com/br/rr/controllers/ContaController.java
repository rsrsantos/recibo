package com.br.rr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.br.rr.exception.NegocioException;
import com.br.rr.service.ContaService;

@Controller
public class ContaController {

	private final ContaService contaService;

	public ContaController(ContaService contaService) {
		this.contaService = contaService;
	}

	@GetMapping("/perfil")
	public String perfil(Model model) {
		model.addAttribute("usuario", contaService.usuarioLogado());
		return "conta/perfil";
	}

	@GetMapping("/alterar-senha")
	public String alterarSenhaForm() {
		return "conta/alterar-senha";
	}

	@PostMapping("/alterar-senha")
	public String alterarSenha(@RequestParam String senhaAtual,
			@RequestParam String novaSenha,
			@RequestParam String confirmacao,
			RedirectAttributes attributes) {
		try {
			contaService.alterarSenha(senhaAtual, novaSenha, confirmacao);
			attributes.addFlashAttribute("success", "Senha alterada com sucesso!");
			return "redirect:/perfil";
		} catch (NegocioException ex) {
			attributes.addFlashAttribute("mensagem_erro", ex.getMessage());
			return "redirect:/alterar-senha";
		}
	}

}
