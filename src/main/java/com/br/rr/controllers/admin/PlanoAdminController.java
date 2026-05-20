package com.br.rr.controllers.admin;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.br.rr.dto.PlanoForm;
import com.br.rr.models.ModeloRecibo;
import com.br.rr.service.PlanoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/planos")
public class PlanoAdminController {

	private final PlanoService service;

	public PlanoAdminController(PlanoService service) {
		this.service = service;
	}

	@GetMapping
	public String listar(Model model, @RequestParam(defaultValue = "0") int page) {
		model.addAttribute("planos",
				service.listar(PageRequest.of(page, 10, Sort.by(Sort.Order.asc("id")))));
		return "admin/planos/lista";
	}

	@GetMapping("/novo")
	public String form(Model model) {
		model.addAttribute("planoForm", new PlanoForm());
		model.addAttribute("modelos", ModeloRecibo.values());
		return "admin/planos/form";
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model) {
		model.addAttribute("planoForm", service.carregarForm(id));
		model.addAttribute("modelos", ModeloRecibo.values());
		return "admin/planos/form";
	}

	@PostMapping
	public String salvar(@Valid @ModelAttribute("planoForm") PlanoForm planoForm,
			BindingResult result, Model model, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			model.addAttribute("modelos", ModeloRecibo.values());
			return "admin/planos/form";
		}
		service.salvarForm(planoForm);
		attributes.addFlashAttribute("success", "Plano salvo com sucesso!");
		return "redirect:/admin/planos";
	}

	@GetMapping("/{id}/excluir")
	public String excluir(@PathVariable Long id, RedirectAttributes attributes) {
		try {
			service.excluir(id);
			attributes.addFlashAttribute("success", "Plano excluído.");
		} catch (Exception ex) {
			attributes.addFlashAttribute("mensagem_erro",
					"Não foi possível excluir: o plano pode estar em uso por assinaturas.");
		}
		return "redirect:/admin/planos";
	}

}
