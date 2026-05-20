package com.br.rr.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.br.rr.dto.EmitenteForm;
import com.br.rr.exception.NegocioException;
import com.br.rr.models.TipoPessoa;
import com.br.rr.service.EmitenteService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/emitente")
public class EmitenteController {

	private final EmitenteService service;

	public EmitenteController(EmitenteService service) {
		this.service = service;
	}

	@GetMapping
	public String form(Model model) {
		model.addAttribute("emitenteForm", service.carregarForm());
		model.addAttribute("tipos", TipoPessoa.values());
		model.addAttribute("temLogo", service.buscarLogo() != null);
		return "emitente/form";
	}

	@PostMapping(consumes = "multipart/form-data")
	public String salvar(@Valid @ModelAttribute EmitenteForm emitenteForm,
			BindingResult result, Model model, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			model.addAttribute("tipos", TipoPessoa.values());
			model.addAttribute("temLogo", service.buscarLogo() != null);
			return "emitente/form";
		}
		service.salvar(emitenteForm);
		MultipartFile logo = emitenteForm.getLogo();
		if (emitenteForm.isRemoverLogo()) {
			service.removerLogo();
		} else if (logo != null && !logo.isEmpty()) {
			try {
				service.salvarLogo(logo);
			} catch (NegocioException ex) {
				attributes.addFlashAttribute("erro", ex.getMessage());
				return "redirect:/emitente";
			}
		}
		attributes.addFlashAttribute("success", "Dados do emitente salvos com sucesso!");
		return "redirect:/emitente";
	}

	/** Serve a logo do emitente autenticado como imagem. */
	@GetMapping("/logo")
	public ResponseEntity<byte[]> logo() {
		byte[] bytes = service.buscarLogo();
		if (bytes == null) {
			return ResponseEntity.notFound().build();
		}
		String mime = service.buscarLogoTipo();
		MediaType mediaType = "image/png".equals(mime) ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;
		return ResponseEntity.ok().contentType(mediaType).body(bytes);
	}

}
