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

import com.br.rr.models.Cliente;
import com.br.rr.service.ClienteService;

@Controller
public class ClienteController {

	private final ClienteService clienteService;

	public ClienteController(ClienteService clienteService) {
		this.clienteService = clienteService;
	}

	@GetMapping("/cadastrarCliente")
	public String form(Cliente cliente) {
		return "cliente/form-cliente";
	}

	@PostMapping("/cadastrarCliente")
	public String cadastrar(@Valid Cliente cliente, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			attributes.addFlashAttribute("mensagem", "Verifique os campos");
			return "redirect:/cadastrarCliente";
		}
		clienteService.salvar(cliente);
		attributes.addFlashAttribute("mensagem", "Cliente cadastrado com sucesso!");
		return "redirect:/cadastrarCliente";
	}

	@GetMapping("/clientes")
	public String listar(Model model, @RequestParam(defaultValue = "0") int page) {
		model.addAttribute("clientes",
				clienteService.listar(PageRequest.of(page, 5, Sort.by(Order.desc("id")))));
		model.addAttribute("currentPage", page);
		return "cliente/lista-cliente";
	}

	@GetMapping("/deletarCliente")
	public String deletar(long id) {
		clienteService.excluir(id);
		return "redirect:/clientes";
	}

	@GetMapping("/editar-cliente")
	public String editarForm(long id, Model model) {
		model.addAttribute("cliente", clienteService.buscarPorId(id));
		return "cliente/update-cliente";
	}

	@PostMapping("/editar-cliente")
	public String editar(@Valid Cliente cliente, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			attributes.addFlashAttribute("mensagem", "Verifique os campos");
			return "redirect:/editar-cliente?id=" + cliente.getId();
		}
		clienteService.salvar(cliente);
		attributes.addFlashAttribute("success", "Cliente alterado com sucesso!");
		return "redirect:/clientes";
	}

}
