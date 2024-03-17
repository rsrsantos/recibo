package com.AppRH.AppRH.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.AppRH.AppRH.models.Produto;
import com.AppRH.AppRH.models.Vaga;

public interface ProdutoRepository extends JpaRepository<Produto, Long>{
	
	Produto findById(long id);
	Produto findByNome(String nome);
//	Produto findByCodigo(long id);

}
