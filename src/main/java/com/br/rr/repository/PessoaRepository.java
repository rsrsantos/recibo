package com.br.rr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.br.rr.models.Pessoa;
import com.br.rr.models.Usuario;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

	Page<Pessoa> findByProprietario(Usuario proprietario, Pageable pageable);

	List<Pessoa> findByNomeContainingIgnoreCaseAndProprietario(String nome, Usuario proprietario);

	Optional<Pessoa> findByIdAndProprietario(Long id, Usuario proprietario);

	long countByProprietario(Usuario proprietario);

}
