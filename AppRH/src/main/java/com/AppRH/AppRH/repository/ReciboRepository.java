package com.AppRH.AppRH.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.AppRH.AppRH.models.Funcionario;
import com.AppRH.AppRH.models.Recibo;


public interface ReciboRepository extends JpaRepository<Recibo, Long>{

	Recibo findById(long id);
	
	

}
