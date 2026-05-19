package com.br.rr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.rr.models.Emitente;
import com.br.rr.models.Usuario;

public interface EmitenteRepository extends JpaRepository<Emitente, Long> {

	Optional<Emitente> findByUsuario(Usuario usuario);

}
