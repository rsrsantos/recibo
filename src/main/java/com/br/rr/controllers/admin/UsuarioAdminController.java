package com.br.rr.controllers.admin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.br.rr.dto.UsuarioEditForm;
import com.br.rr.exception.NegocioException;
import com.br.rr.models.Usuario;
import com.br.rr.service.UsuarioAdminService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioAdminController {

	private final UsuarioAdminService service;

	public UsuarioAdminController(UsuarioAdminService service) {
		this.service = service;
	}

	@GetMapping
	public String listar(Model model, @RequestParam(defaultValue = "0") int page) {
		Page<Usuario> usuarios =
				service.listar(PageRequest.of(page, 10, Sort.by(Sort.Order.asc("id"))));
		Map<Long, String> planos = new HashMap<>();
		Map<Long, String> perfis = new HashMap<>();
		for (Usuario u : usuarios) {
			planos.put(u.getId(), service.planoAtivoNome(u));
			perfis.put(u.getId(), service.perfilAtualNome(u));
		}
		model.addAttribute("usuarios", usuarios);
		model.addAttribute("planos", planos);
		model.addAttribute("perfis", perfis);
		model.addAttribute("perfisDisponiveis", service.listarPerfis());
		return "admin/usuarios/lista";
	}

	@GetMapping("/{id}/alternar")
	public String alternar(@PathVariable Long id, RedirectAttributes attributes) {
		try {
			service.alternarAtivo(id);
			attributes.addFlashAttribute("success", "Status do usuário atualizado.");
		} catch (NegocioException ex) {
			attributes.addFlashAttribute("mensagem_erro", ex.getMessage());
		}
		return "redirect:/admin/usuarios";
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model) {
		model.addAttribute("usuarioEditForm", service.carregarEdicao(id));
		return "admin/usuarios/form";
	}

	@PostMapping("/{id}")
	public String salvar(@PathVariable Long id,
			@Valid @ModelAttribute("usuarioEditForm") UsuarioEditForm usuarioEditForm,
			BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return "admin/usuarios/form";
		}
		try {
			usuarioEditForm.setId(id);
			service.salvarEdicao(usuarioEditForm);
			attributes.addFlashAttribute("success", "Usuário atualizado com sucesso.");
			return "redirect:/admin/usuarios";
		} catch (NegocioException ex) {
			attributes.addFlashAttribute("mensagem_erro", ex.getMessage());
			return "redirect:/admin/usuarios/" + id + "/editar";
		}
	}

	@PostMapping("/{id}/perfil")
	public String alterarPerfil(@PathVariable Long id, @RequestParam Long perfilId,
			RedirectAttributes attributes) {
		try {
			service.alterarPerfil(id, perfilId);
			attributes.addFlashAttribute("success", "Perfil do usuário atualizado.");
		} catch (NegocioException ex) {
			attributes.addFlashAttribute("mensagem_erro", ex.getMessage());
		}
		return "redirect:/admin/usuarios";
	}

}
