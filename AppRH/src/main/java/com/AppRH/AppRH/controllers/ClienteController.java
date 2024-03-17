package com.AppRH.AppRH.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.AppRH.AppRH.models.Cliente;
import com.AppRH.AppRH.models.Funcionario;
import com.AppRH.AppRH.models.Produto;
import com.AppRH.AppRH.models.Recibo;
import com.AppRH.AppRH.repository.ClienteRepository;

@Controller
public class ClienteController {

	@Autowired
	ClienteRepository clienteController;

	// GET que chama o form para cadastrar clientes
	@RequestMapping("/cadastrarCliente")
	public String form() {
		return "cliente/form-cliente";
	}

	// POST que cadastra clientes
	@RequestMapping(value = "/cadastrarCliente", method = RequestMethod.POST)
	public String form(@Valid Cliente cliente, BindingResult result, RedirectAttributes attributes) {

		if (result.hasErrors()) {
			attributes.addFlashAttribute("mensagem", "Verifique os campos");
			return "redirect:/cadastrarCliente";
		}

		clienteController.save(cliente);
		attributes.addFlashAttribute("mensagem", "Cliente cadastrado com sucesso!");
		return "redirect:/cadastrarCliente";
	}

	// GET que lista clientes
//	@RequestMapping("/clientes")
//	public ModelAndView listaClientes() {
//		ModelAndView mv = new ModelAndView("cliente/lista-cliente");
//		Iterable<Cliente> clientes = clienteController.findAll();
//		mv.addObject("clientes", clientes);
//		return mv;
//	}

	@GetMapping("/clientes")
	public String showPage(Model model, @RequestParam(defaultValue = "0") int page) {
		model.addAttribute("clientes", clienteController.findAll(PageRequest.of(page, 5, Sort.by(Order.desc("id")))));
		model.addAttribute("currentPage", page);
		return "cliente/lista-cliente";
	}

	// GET que deleta cliente
	@RequestMapping("/deletarCliente")
	public String deletarCliente(long id) {
		Cliente cliente = clienteController.findById(id);
		clienteController.delete(cliente);
		return "redirect:/clientes";
	}
	
	@RequestMapping("/editar-cliente")
	public ModelAndView editarCliente(long id) {
		Cliente cliente = clienteController.findById(id);
		ModelAndView mv = new ModelAndView("cliente/update-cliente");
		mv.addObject("cliente", cliente);
		return mv;
	}
	
	// POST do FORM que atualiza Cliente
			@RequestMapping(value = "/editar-cliente", method = RequestMethod.POST)
			public String updateCliente(@Valid Cliente cliente, BindingResult result, RedirectAttributes attributes) {
				clienteController.save(cliente);
				attributes.addFlashAttribute("success", "Produto alterado com sucesso!");
				return "redirect:/clientes/";
			}

}
