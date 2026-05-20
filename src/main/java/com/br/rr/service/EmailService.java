package com.br.rr.service;

public interface EmailService {
    void enviarConfirmacaoCadastro(String destinatario, String nome, String token);
}
