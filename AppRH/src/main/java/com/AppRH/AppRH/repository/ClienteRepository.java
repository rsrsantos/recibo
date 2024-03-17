package com.AppRH.AppRH.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.AppRH.AppRH.models.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{

	Cliente findById(long id);
	Cliente findByNome(String nome);
	
}
