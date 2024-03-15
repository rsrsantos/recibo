package com.AppRH.AppRH.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.AppRH.AppRH.models.Cliente;
import com.AppRH.AppRH.models.Funcionario;
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

	// GET que lista recibos
	@RequestMapping("/clientes")
	public ModelAndView listaClientes() {
		ModelAndView mv = new ModelAndView("cliente/lista-cliente");
		Iterable<Cliente> clientes = clienteController.findAll();
		mv.addObject("clientes", clientes);
		return mv;
	}

	// GET que deleta cliente
	@RequestMapping("/deletarCliente")
	public String deletarCliente(long id) {
		Cliente cliente = clienteController.findById(id);
		clienteController.delete(cliente);
		return "redirect:/clientes";

	}

}
