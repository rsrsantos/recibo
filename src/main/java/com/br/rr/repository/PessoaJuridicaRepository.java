package com.br.rr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.rr.models.PessoaJuridica;

public interface PessoaJuridicaRepository extends JpaRepository<PessoaJuridica, Long> {

	Optional<PessoaJuridica> findByPessoaId(Long pessoaId);

}
