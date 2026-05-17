package com.br.rr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.br.rr.service.ClienteService;
import com.br.rr.service.ProdutoService;
import com.br.rr.service.ReciboService;

@Controller
public class HomeController {

	private final ClienteService clienteService;
	private final ProdutoService produtoService;
	private final ReciboService reciboService;

	public HomeController(ClienteService clienteService, ProdutoService produtoService,
			ReciboService reciboService) {
		this.clienteService = clienteService;
		this.produtoService = produtoService;
		this.reciboService = reciboService;
	}

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("totalClientes", clienteService.contar());
		model.addAttribute("totalProdutos", produtoService.contar());
		model.addAttribute("totalRecibos", reciboService.contar());
		model.addAttribute("valorTotalRecibos", reciboService.somaValorTotal());
		model.addAttribute("ultimosRecibos", reciboService.ultimos(5));
		return "index";
	}

	@PostMapping("/")
	public String buscar(@RequestParam("buscar") String termo,
			@RequestParam(value = "tipo", defaultValue = "cliente") String tipo, Model model) {

		if ("produto".equals(tipo)) {
			model.addAttribute("produtos", produtoService.buscarPorNome(termo));
		} else {
			model.addAttribute("clientes", clienteService.buscarPorNome(termo));
		}
		model.addAttribute("aviso", "Resultados da busca por \"" + termo + "\"");
		model.addAttribute("totalClientes", clienteService.contar());
		model.addAttribute("totalProdutos", produtoService.contar());
		model.addAttribute("totalRecibos", reciboService.contar());
		model.addAttribute("valorTotalRecibos", reciboService.somaValorTotal());
		model.addAttribute("ultimosRecibos", reciboService.ultimos(5));
		return "index";
	}

}
