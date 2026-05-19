package com.br.rr.controllers.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.br.rr.models.UsuarioPlano;
import com.br.rr.repository.UsuarioPlanoRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private final UsuarioPlanoRepository usuarioPlanoRepository;

	public AdminController(UsuarioPlanoRepository usuarioPlanoRepository) {
		this.usuarioPlanoRepository = usuarioPlanoRepository;
	}

	@GetMapping
	public String painel(Model model) {
		List<UsuarioPlano> ativos = usuarioPlanoRepository.findByStatus("ATIVO");
		LocalDate hoje = LocalDate.now();

		long gratuitos = 0, emCarencia = 0, vencidos = 0;
		BigDecimal receitaEstimada = BigDecimal.ZERO;

		for (UsuarioPlano up : ativos) {
			BigDecimal preco = up.getPlano().getPrecoMensal();
			boolean pago = preco != null && preco.signum() > 0;

			if (up.getDtFim() == null) {
				gratuitos++;
			} else if (up.getDtFim().isBefore(hoje)) {
				vencidos++;
			} else {
				emCarencia++;
			}

			if (pago && (up.getDtFim() == null || !up.getDtFim().isBefore(hoje))) {
				receitaEstimada = receitaEstimada.add(preco);
			}
		}

		model.addAttribute("totalAtivas", ativos.size());
		model.addAttribute("gratuitos", gratuitos);
		model.addAttribute("emCarencia", emCarencia);
		model.addAttribute("vencidos", vencidos);
		model.addAttribute("receitaEstimada", receitaEstimada);
		return "admin/painel";
	}

}
