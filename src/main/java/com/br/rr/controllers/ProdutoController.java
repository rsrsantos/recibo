package com.br.rr.controllers;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.br.rr.models.Produto;
import com.br.rr.service.ProdutoService;

@Controller
public class ProdutoController {

	private final ProdutoService produtoService;

	public ProdutoController(ProdutoService produtoService) {
		this.produtoService = produtoService;
	}

	@GetMapping("/cadastrarProduto")
	public String form(Produto produto) {
		return "produto/form-produto";
	}

	@PostMapping("/cadastrarProduto")
	public String cadastrar(@Valid Produto produto, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			attributes.addFlashAttribute("mensagem", "Verifique os campos");
			return "redirect:/cadastrarProduto";
		}
		produtoService.salvar(produto);
		attributes.addFlashAttribute("mensagem", "Produto cadastrado com sucesso!");
		return "redirect:/cadastrarProduto";
	}

	@GetMapping("/produtos")
	public String listar(Model model, @RequestParam(defaultValue = "0") int page) {
		model.addAttribute("produtos",
				produtoService.listar(PageRequest.of(page, 5, Sort.by(Order.desc("id")))));
		model.addAttribute("currentPage", page);
		return "produto/lista-produto";
	}

	@GetMapping("/deletarProduto")
	public String deletar(long id) {
		produtoService.excluir(id);
		return "redirect:/produtos";
	}

	@GetMapping("/editar-produto")
	public String editarForm(long id, Model model) {
		model.addAttribute("produto", produtoService.buscarPorId(id));
		return "produto/update-produto";
	}

	@PostMapping("/editar-produto")
	public String editar(@Valid Produto produto, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			attributes.addFlashAttribute("mensagem", "Verifique os campos");
			return "redirect:/editar-produto?id=" + produto.getId();
		}
		produtoService.salvar(produto);
		attributes.addFlashAttribute("success", "Produto alterado com sucesso!");
		return "redirect:/produtos";
	}

}
