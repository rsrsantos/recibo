package com.br.rr.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.br.rr.models.Recibo;

public interface ReciboRepository extends JpaRepository<Recibo, Long> {

	@Query("select coalesce(sum(r.valorTotal), 0) from Recibo r")
	double somaValorTotal();

	@Query("select r from Recibo r order by r.id desc")
	List<Recibo> ultimos(Pageable pageable);

}
