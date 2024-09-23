package br.ufpb.dcx.rodrigor.projetos.login.service;

import br.ufpb.dcx.rodrigor.projetos.login.controller.PerfilController;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EmailService {
    private static final Logger logger = LogManager.getLogger(EmailService.class);
    private final String smtpHost = "smtp.gmail.com";
    private final String username = "authpoogrupog2@gmail.com";
    private final String password = "lnij nibn xkrm xbbu"; // Senha de aplicativo gerada
    private final int smtpPort = 587;

    public void enviarEmail(String toEmail, String subject, String message) throws EmailException {
        // Configuração do email
        Email email = new SimpleEmail();
        email.setHostName(smtpHost);
        email.setSmtpPort(smtpPort);
        email.setAuthentication(username, password);
        email.setStartTLSEnabled(true); // Habilitar TLS para segurança
        email.setFrom(username); // Remetente
        email.setSubject(subject); // Assunto do e-mail
        email.setMsg(message); // Mensagem do e-mail
        email.addTo(toEmail); // Destinatário

        // Enviar o e-mail
        email.send();
        logger.info("E-mail enviado com sucesso!");
    }
}
