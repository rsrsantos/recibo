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

//	// GET que lista produtos
//	@RequestMapping("/produtos")
//	public ModelAndView listaProdutos() {
//		ModelAndView mv = new ModelAndView("produto/lista-produto");
//		Iterable<Produto> produtos = produtoRepository.findAll();
//		mv.addObject("produtos", produtos);
//		return mv;
//	}

	@GetMapping("/produtos")
	public String showPage(Model model, @RequestParam(defaultValue = "0") int page) {
		model.addAttribute("produtos", produtoRepository.findAll(PageRequest.of(page, 5, Sort.by(Order.desc("id")))));
		model.addAttribute("currentPage", page);
		return "produto/lista-produto";
	}

	// GET que deleta produto
	@RequestMapping("/deletarProduto")
	public String deletarProduto(long id) {
		Produto produto = produtoRepository.findById(id);
		produtoRepository.delete(produto);
		return "redirect:/produtos";

	}

	@RequestMapping("/editar-produto")
	public ModelAndView editarProduto(long id) {
		Produto produto = produtoRepository.findById(id);
		ModelAndView mv = new ModelAndView("produto/update-produto");
		mv.addObject("produto", produto);
		return mv;
	}

	// POST do FORM que atualiza o Produto
	@RequestMapping(value = "/editar-produto", method = RequestMethod.POST)
	public String updateProduto(@Valid Produto produto, BindingResult result, RedirectAttributes attributes) {
		produtoRepository.save(produto);
		attributes.addFlashAttribute("success", "Produto alterado com sucesso!");
//
//			long codigoLong = produto.getId();
//			String codigo = "" + codigoLong;
		return "redirect:/produtos/";
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
