package com.br.rr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.rr.models.PessoaFisica;

public interface PessoaFisicaRepository extends JpaRepository<PessoaFisica, Long> {

	Optional<PessoaFisica> findByPessoaId(Long pessoaId);

}
