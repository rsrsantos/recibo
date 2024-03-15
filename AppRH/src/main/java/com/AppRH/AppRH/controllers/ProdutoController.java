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
import com.AppRH.AppRH.models.Produto;
import com.AppRH.AppRH.repository.ProdutoRepository;

@Controller
public class ProdutoController {

	@Autowired
	ProdutoRepository produtoRepository;
	
	
	// GET que chama o form para cadastrar produtos
		@RequestMapping("/cadastrarProduto")
		public String form() {
			return "produto/form-produto";
		}

	// GET que lista produtos
	@RequestMapping("/produtos")
	public ModelAndView listaProdutos() {
		ModelAndView mv = new ModelAndView("produto/lista-produto");
		Iterable<Produto> produtos = produtoRepository.findAll();
		mv.addObject("produtos", produtos);
		return mv;
	}

	// GET que deleta produto
	@RequestMapping("/deletarProduto")
	public String deletarProduto(long id) {
		Produto produto = produtoRepository.findById(id);
		produtoRepository.delete(produto);
		return "redirect:/produtos";

	}
	
	// POST que cadastra produtos
		@RequestMapping(value = "/cadastrarProduto", method = RequestMethod.POST)
		public String form(@Valid Produto produto, BindingResult result, RedirectAttributes attributes) {

			if (result.hasErrors()) {
				attributes.addFlashAttribute("mensagem", "Verifique os campos");
				return "redirect:/cadastrarProduto";
			}

			produtoRepository.save(produto);
			attributes.addFlashAttribute("mensagem", "Produto cadastrado com sucesso!");
			return "redirect:/cadastrarProduto";
		}
}
