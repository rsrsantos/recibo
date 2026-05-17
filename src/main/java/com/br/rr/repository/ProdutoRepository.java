package com.br.rr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.rr.models.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

	List<Produto> findByNomeContainingIgnoreCase(String nome);

}
