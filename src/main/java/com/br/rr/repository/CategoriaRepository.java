package com.br.rr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.br.rr.models.Categoria;
import com.br.rr.models.Usuario;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

	Page<Categoria> findByUsuario(Usuario usuario, Pageable pageable);

	List<Categoria> findByUsuarioAndAtivoTrue(Usuario usuario);

	Optional<Categoria> findByIdAndUsuario(Long id, Usuario usuario);

	long countByUsuario(Usuario usuario);

}
