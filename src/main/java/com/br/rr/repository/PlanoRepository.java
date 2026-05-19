package com.br.rr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.rr.models.Plano;

public interface PlanoRepository extends JpaRepository<Plano, Long> {

	List<Plano> findByAtivoTrue();

}
