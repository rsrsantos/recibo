package com.AppRH.AppRH.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Recibo {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "empresa_id")
	Empresa empresa;
	
	@ManyToOne
	@JoinColumn(name = "cliente_id")
	Cliente cliente;
	
	Integer numeroRecibo;
	
	@CreationTimestamp
	@DateTimeFormat(pattern="dd-MMM-YYYY")
	Date dataInclusao;
}
