package com.br.rr.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.br.rr.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.remetente}")
    private String remetente;

    @Value("${app.url-base}")
    private String urlBase;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarConfirmacaoCadastro(String destinatario, String nome, String token) {
        String link = urlBase + "/confirmar-email?token=" + token;

        String corpo = """
                <html><body style="font-family:sans-serif;color:#333">
                  <h2>Confirme seu cadastro</h2>
                  <p>Olá, <strong>%s</strong>!</p>
                  <p>Clique no botão abaixo para ativar sua conta. O link é válido por <strong>24 horas</strong>.</p>
                  <p style="margin:32px 0">
                    <a href="%s"
                       style="background:#206bc4;color:#fff;padding:12px 28px;border-radius:6px;text-decoration:none;font-size:15px">
                      Confirmar e-mail
                    </a>
                  </p>
                  <p style="font-size:13px;color:#888">
                    Se você não se cadastrou no Recibos, ignore este e-mail.<br>
                    Ou copie e cole o link: <a href="%s">%s</a>
                  </p>
                </body></html>
                """.formatted(nome, link, link, link);

        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(remetente);
            helper.setTo(destinatario);
            helper.setSubject("Confirme seu cadastro — Recibos");
            helper.setText(corpo, true);
            mailSender.send(msg);
        } catch (MessagingException | MailException e) {
            // Loga mas não interrompe o fluxo — usuário pode reenviar depois
            throw new RuntimeException("Falha ao enviar e-mail de confirmação.", e);
        }
    }
}
