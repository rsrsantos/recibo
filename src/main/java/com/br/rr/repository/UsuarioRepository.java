package com.br.rr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.br.rr.models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByEmail(String email);

	Optional<Usuario> findByGoogleId(String googleId);

	@Query("select count(u) from Usuario u join u.perfis p where p.id = :perfilId")
	long contarPorPerfil(@Param("perfilId") Long perfilId);

}
