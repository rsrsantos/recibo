package com.br.rr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.rr.models.Pagamento;
import com.br.rr.models.Plano;
import com.br.rr.models.Usuario;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

	Optional<Pagamento> findByMpPaymentId(String mpPaymentId);

	boolean existsByUsuarioAndPlanoAndMpStatus(Usuario usuario, Plano plano, String mpStatus);

	java.util.List<Pagamento> findTop10ByUsuarioOrderByCriadoEmDesc(Usuario usuario);

}
