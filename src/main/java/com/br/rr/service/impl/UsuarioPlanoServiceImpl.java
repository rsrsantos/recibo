package com.br.rr.service.impl;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.rr.exception.RecursoNaoEncontradoException;
import com.br.rr.models.Plano;
import com.br.rr.models.Usuario;
import com.br.rr.models.UsuarioPlano;
import com.br.rr.repository.PlanoRepository;
import com.br.rr.repository.UsuarioPlanoRepository;
import com.br.rr.service.UsuarioPlanoService;

@Service
@Transactional
public class UsuarioPlanoServiceImpl implements UsuarioPlanoService {

	private final UsuarioPlanoRepository repository;
	private final PlanoRepository planoRepository;

	public UsuarioPlanoServiceImpl(UsuarioPlanoRepository repository, PlanoRepository planoRepository) {
		this.repository = repository;
		this.planoRepository = planoRepository;
	}

	@Override
	public UsuarioPlano ativar(Usuario usuario, Long planoId) {
		Plano plano = planoRepository.findById(planoId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Plano não encontrado: " + planoId));

		// Cancela plano ativo anterior, se houver
		repository.findFirstByUsuarioAndStatusOrderByIdDesc(usuario, "ATIVO")
				.ifPresent(p -> { p.setStatus("CANCELADO"); repository.save(p); });

		UsuarioPlano up = new UsuarioPlano();
		up.setUsuario(usuario);
		up.setPlano(plano);
		up.setDtInicio(LocalDate.now());
		up.setStatus("ATIVO");

		// Carência habilitada: dtFim = hoje + carenciaDias.
		// 0 dias = sem período gratuito = bloqueia imediatamente (dtFim = hoje).
		// Sem carência (temCarencia=false): dtFim nulo = acesso indefinido.
		if (plano.isTemCarencia()) {
			up.setDtFim(LocalDate.now().plusDays(plano.getCarenciaDias()));
		}

		return repository.save(up);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<UsuarioPlano> buscarAtivo(Usuario usuario) {
		return repository.findFirstByUsuarioAndStatusOrderByIdDesc(usuario, "ATIVO");
	}

}
