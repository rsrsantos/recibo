package com.br.rr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.br.rr.exception.NegocioException;
import com.br.rr.service.ContaService;

@Controller
public class ConfirmacaoController {

    private final ContaService contaService;

    public ConfirmacaoController(ContaService contaService) {
        this.contaService = contaService;
    }

    @GetMapping("/confirmar-email")
    public String confirmar(@RequestParam String token, Model model) {
        try {
            contaService.confirmarEmail(token);
            model.addAttribute("sucesso", true);
        } catch (NegocioException ex) {
            model.addAttribute("erro", ex.getMessage());
        }
        return "cadastro/confirmacao-resultado";
    }

    @PostMapping("/reenviar-confirmacao")
    public String reenviar(@RequestParam String email, RedirectAttributes ra) {
        String emailEnc = java.net.URLEncoder.encode(
                email != null ? email : "", java.nio.charset.StandardCharsets.UTF_8);
        try {
            contaService.reenviarConfirmacao(email);
            ra.addFlashAttribute("mensagem", "E-mail reenviado! Verifique sua caixa de entrada.");
        } catch (NegocioException ex) {
            ra.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/cadastro/aguardando-confirmacao?email=" + emailEnc;
    }

}
