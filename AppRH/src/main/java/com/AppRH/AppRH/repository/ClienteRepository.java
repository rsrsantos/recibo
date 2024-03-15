package com.AppRH.AppRH.repository;

import org.springframework.data.repository.CrudRepository;

import com.AppRH.AppRH.models.Cliente;
import com.AppRH.AppRH.models.Funcionario;

public interface ClienteRepository extends CrudRepository<Cliente, Long>{

	Cliente findById(long id);
	Cliente findByNome(String nome);
	
}
