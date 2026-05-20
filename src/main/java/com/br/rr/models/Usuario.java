package com.br.rr.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 120)
	private String email;

	@Column(name = "senha_hash", length = 100)
	private String senhaHash;

	/** ID retornado pelo Google (sub). Nulo para usuários com login por senha. */
	@Column(name = "google_id", unique = true)
	private String googleId;

	private boolean ativo = true;

	@Column(name = "email_confirmado", nullable = false)
	private boolean emailConfirmado = false;

	/** Token de ativação gerado no cadastro. Nulo após confirmação. */
	@Column(name = "token_confirmacao", unique = true, length = 64)
	private String tokenConfirmacao;

	@Column(name = "token_expira_em")
	private LocalDateTime tokenExpiraEm;

	@CreationTimestamp
	@Column(name = "criado_em", nullable = false, updatable = false)
	private LocalDateTime criadoEm;

	@OneToOne
	@JoinColumn(name = "pessoa_id", unique = true)
	private Pessoa pessoa;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "usuario_perfil",
			joinColumns = @JoinColumn(name = "usuario_id"),
			inverseJoinColumns = @JoinColumn(name = "perfil_id"))
	private Set<Perfil> perfis = new HashSet<>();

}
