package com.br.rr.controllers;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.br.rr.dto.CadastroForm;
import com.br.rr.exception.NegocioException;
import com.br.rr.models.Usuario;
import com.br.rr.security.UsuarioDetailsService;
import com.br.rr.service.ContaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/cadastro")
public class CadastroController {

	private final ContaService contaService;
	private final UsuarioDetailsService userDetailsService;

	public CadastroController(ContaService contaService, UsuarioDetailsService userDetailsService) {
		this.contaService = contaService;
		this.userDetailsService = userDetailsService;
	}

	@GetMapping
	public String form(@RequestParam(required = false) Long plano, Model model) {
		CadastroForm form = new CadastroForm();
		form.setPlanoId(plano);
		model.addAttribute("cadastroForm", form);
		return "cadastro";
	}

	@PostMapping
	public String cadastrar(@Valid @ModelAttribute CadastroForm cadastroForm,
			BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "cadastro";
		}
		try {
			Usuario usuario = contaService.cadastrar(cadastroForm);
			autoLogin(usuario.getEmail());

			if (cadastroForm.getPlanoId() != null) {
				return "redirect:/planos/ativar?plano=" + cadastroForm.getPlanoId();
			}
			return "redirect:/";
		} catch (NegocioException ex) {
			model.addAttribute("erro", ex.getMessage());
			return "cadastro";
		}
	}

	private void autoLogin(String email) {
		UserDetails details = userDetailsService.loadUserByUsername(email);
		UsernamePasswordAuthenticationToken auth =
				new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

}
