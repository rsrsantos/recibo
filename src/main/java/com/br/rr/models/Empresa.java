package com.br.rr.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Empresa {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="razao_social")
	String razaoSocial;
	
	@Column(name="nome_fantasia")
	String nomeFantasia;
	
	String cnpj;
	String telefone;
	String endereco;
	String bairro;
	String cep;
	byte[] logo; 

}
