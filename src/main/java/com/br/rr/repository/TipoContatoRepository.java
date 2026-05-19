package com.br.rr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.rr.models.TipoContato;

public interface TipoContatoRepository extends JpaRepository<TipoContato, Long> {

	List<TipoContato> findByAtivoTrue();

}
