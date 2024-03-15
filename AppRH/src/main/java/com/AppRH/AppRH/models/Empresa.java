package com.AppRH.AppRH.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
