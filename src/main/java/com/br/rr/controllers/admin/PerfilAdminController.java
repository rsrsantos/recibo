package com.br.rr.controllers.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.br.rr.dto.PerfilForm;
import com.br.rr.exception.NegocioException;
import com.br.rr.models.Perfil;
import com.br.rr.service.PerfilService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/perfis")
public class PerfilAdminController {

	private final PerfilService service;

	public PerfilAdminController(PerfilService service) {
		this.service = service;
	}

	@GetMapping
	public String listar(Model model) {
		List<Perfil> perfis = service.listar();
		Map<Long, Long> usuariosPorPerfil = new HashMap<>();
		Map<Long, Boolean> sistema = new HashMap<>();
		for (Perfil p : perfis) {
			usuariosPorPerfil.put(p.getId(), service.contarUsuarios(p.getId()));
			sistema.put(p.getId(), service.isSistema(p.getNome()));
		}
		model.addAttribute("perfis", perfis);
		model.addAttribute("usuariosPorPerfil", usuariosPorPerfil);
		model.addAttribute("sistema", sistema);
		return "admin/perfis/lista";
	}

	@GetMapping("/novo")
	public String form(Model model) {
		model.addAttribute("perfilForm", new PerfilForm());
		return "admin/perfis/form";
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model, RedirectAttributes attributes) {
		PerfilForm form = service.carregarForm(id);
		if (service.isSistema(form.getNome())) {
			attributes.addFlashAttribute("mensagem_erro",
					"O perfil de sistema \"" + form.getNome() + "\" não pode ser editado.");
			return "redirect:/admin/perfis";
		}
		model.addAttribute("perfilForm", form);
		return "admin/perfis/form";
	}

	@PostMapping
	public String salvar(@Valid @ModelAttribute("perfilForm") PerfilForm perfilForm,
			BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return "admin/perfis/form";
		}
		try {
			service.salvarForm(perfilForm);
			attributes.addFlashAttribute("success", "Perfil salvo com sucesso.");
		} catch (NegocioException ex) {
			attributes.addFlashAttribute("mensagem_erro", ex.getMessage());
		}
		return "redirect:/admin/perfis";
	}

	@GetMapping("/{id}/excluir")
	public String excluir(@PathVariable Long id, RedirectAttributes attributes) {
		try {
			service.excluir(id);
			attributes.addFlashAttribute("success", "Perfil excluído.");
		} catch (NegocioException ex) {
			attributes.addFlashAttribute("mensagem_erro", ex.getMessage());
		}
		return "redirect:/admin/perfis";
	}

}
