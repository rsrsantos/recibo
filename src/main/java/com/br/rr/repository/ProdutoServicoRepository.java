package com.br.rr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.br.rr.models.ProdutoServico;
import com.br.rr.models.Usuario;

public interface ProdutoServicoRepository extends JpaRepository<ProdutoServico, Long> {

	Page<ProdutoServico> findByUsuario(Usuario usuario, Pageable pageable);

	List<ProdutoServico> findByUsuarioAndAtivoTrue(Usuario usuario);

	Optional<ProdutoServico> findByIdAndUsuario(Long id, Usuario usuario);

	long countByUsuario(Usuario usuario);

}
