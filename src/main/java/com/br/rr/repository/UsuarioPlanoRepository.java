package com.br.rr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.rr.models.Usuario;
import com.br.rr.models.UsuarioPlano;

public interface UsuarioPlanoRepository extends JpaRepository<UsuarioPlano, Long> {

	List<UsuarioPlano> findByUsuarioOrderByIdDesc(Usuario usuario);

	Optional<UsuarioPlano> findFirstByUsuarioAndStatusOrderByIdDesc(Usuario usuario, String status);

	List<UsuarioPlano> findByStatus(String status);

}
