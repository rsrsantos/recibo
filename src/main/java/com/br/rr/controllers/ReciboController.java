package com.br.rr.controllers;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.br.rr.dto.ReciboForm;
import com.br.rr.exception.NegocioException;
import com.br.rr.models.Recibo;
import com.br.rr.service.ClienteService;
import com.br.rr.service.ProdutoService;
import com.br.rr.service.ReciboService;

@Controller
public class ReciboController {

	private final ReciboService reciboService;
	private final ClienteService clienteService;
	private final ProdutoService produtoService;

	public ReciboController(ReciboService reciboService, ClienteService clienteService,
			ProdutoService produtoService) {
		this.reciboService = reciboService;
		this.clienteService = clienteService;
		this.produtoService = produtoService;
	}

	@GetMapping("/recibos")
	public String listar(Model model, @RequestParam(defaultValue = "0") int page) {
		model.addAttribute("recibos",
				reciboService.listar(PageRequest.of(page, 5, Sort.by(Order.desc("id")))));
		model.addAttribute("currentPage", page);
		return "recibo/lista-recibo";
	}

	@GetMapping("/cadastrarRecibo")
	public String form(Model model) {
		model.addAttribute("reciboForm", new ReciboForm());
		model.addAttribute("clientes", clienteService.listarTodos());
		model.addAttribute("produtos", produtoService.listarTodos());
		return "recibo/form-recibo";
	}

	@PostMapping("/cadastrarRecibo")
	public String emitir(@ModelAttribute ReciboForm reciboForm, RedirectAttributes attributes) {
		try {
			Recibo salvo = reciboService.emitir(reciboForm);
			attributes.addFlashAttribute("mensagem",
					"Recibo nº " + salvo.getNumeroRecibo() + " emitido com sucesso!");
			return "redirect:/recibos";
		} catch (NegocioException ex) {
			attributes.addFlashAttribute("mensagem_erro", ex.getMessage());
			return "redirect:/cadastrarRecibo";
		}
	}

	@GetMapping("/deletarRecibo")
	public String deletar(long id) {
		reciboService.excluir(id);
		return "redirect:/recibos";
	}

}
