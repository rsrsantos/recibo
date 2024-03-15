package com.AppRH.AppRH.repository;

import org.springframework.data.repository.CrudRepository;

import com.AppRH.AppRH.models.Produto;

public interface ProdutoRepository extends CrudRepository<Produto, Long>{
	
	Produto findById(long id);
	Produto findByNome(String nome);

}
