package com.br.rr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.rr.models.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

	List<Cliente> findByNomeContainingIgnoreCase(String nome);

}
