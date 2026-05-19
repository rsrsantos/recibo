package com.br.rr.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.br.rr.dto.PessoaForm;
import com.br.rr.models.Pessoa;
import com.br.rr.models.TipoPessoa;
import com.br.rr.service.PessoaService;

@Controller
public class PessoaController {

	private final PessoaService service;

	public PessoaController(PessoaService service) {
		this.service = service;
	}

	@GetMapping("/pessoas")
	public String listar(Model model, @RequestParam(defaultValue = "0") int page) {
		model.addAttribute("pessoas",
				service.listar(PageRequest.of(page, 5, Sort.by(Sort.Order.desc("id")))));
		return "pessoa/lista";
	}

	@GetMapping("/pessoas/nova")
	public String form(Model model) {
		model.addAttribute("pessoaForm", new PessoaForm());
		model.addAttribute("tipos", TipoPessoa.values());
		return "pessoa/form";
	}

	@PostMapping("/pessoas")
	public String salvar(@Valid @org.springframework.web.bind.annotation.ModelAttribute("pessoaForm") PessoaForm pessoaForm,
			BindingResult result, Model model, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			model.addAttribute("tipos", TipoPessoa.values());
			return "pessoa/form";
		}
		service.salvar(pessoaForm);
		attributes.addFlashAttribute("success", "Pessoa salva com sucesso!");
		return "redirect:/pessoas";
	}

	@GetMapping("/pessoas/{id}/editar")
	public String editar(@PathVariable Long id, Model model) {
		model.addAttribute("pessoaForm", service.carregarForm(id));
		model.addAttribute("tipos", TipoPessoa.values());
		return "pessoa/form";
	}

	@GetMapping("/pessoas/{id}/excluir")
	public String excluir(@PathVariable Long id, RedirectAttributes attributes) {
		service.excluir(id);
		attributes.addFlashAttribute("success", "Pessoa excluída.");
		return "redirect:/pessoas";
	}

	// ---- Endpoints JSON (busca dinâmica + cadastro rápido no recibo) ----

	@GetMapping("/pessoas/buscar")
	@ResponseBody
	public List<Map<String, Object>> buscar(@RequestParam("q") String q) {
		return service.buscar(q).stream()
				.limit(10)
				.map(this::toJson)
				.collect(Collectors.toList());
	}

	@PostMapping("/pessoas/rapido")
	@ResponseBody
	public Map<String, Object> criarRapido(@RequestParam String nome,
			@RequestParam(defaultValue = "FISICA") TipoPessoa tipo,
			@RequestParam(required = false) String documento) {
		return toJson(service.criarRapido(nome, tipo, documento));
	}

	private Map<String, Object> toJson(Pessoa p) {
		Map<String, Object> m = new java.util.HashMap<>();
		m.put("id", p.getId());
		m.put("nome", p.getNome());
		m.put("tipo", p.getTipo());
		return m;
	}

}
