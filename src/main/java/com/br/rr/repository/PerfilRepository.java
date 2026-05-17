package com.br.rr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.rr.models.Perfil;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {

	Optional<Perfil> findByNome(String nome);

}
