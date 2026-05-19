package com.br.rr.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.br.rr.models.Recibo;
import com.br.rr.models.Usuario;

public interface ReciboRepository extends JpaRepository<Recibo, Long> {

	Page<Recibo> findByUsuario(Usuario usuario, Pageable pageable);

	Optional<Recibo> findByIdAndUsuario(Long id, Usuario usuario);

	long countByUsuario(Usuario usuario);

	@Query("select count(r) from Recibo r where r.usuario = :usuario "
			+ "and r.dataGeracao >= :inicio and r.dataGeracao <= :fim")
	long countNoPeriodo(@Param("usuario") Usuario usuario,
			@Param("inicio") java.time.LocalDate inicio,
			@Param("fim") java.time.LocalDate fim);

	@Query("select coalesce(sum(r.vlrTotal), 0) from Recibo r where r.usuario = :usuario")
	BigDecimal somaTotalPorUsuario(@Param("usuario") Usuario usuario);

	@Query("select r from Recibo r where r.usuario = :usuario order by r.id desc")
	List<Recibo> ultimos(@Param("usuario") Usuario usuario, Pageable pageable);

}
