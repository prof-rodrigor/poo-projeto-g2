package br.ufpb.dcx.rodrigor.projetos.login.service;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailService {

    public void enviarEmail(String destinatario, String assunto, String conteudo) throws MessagingException {
        // Configuração das propriedades do servidor SMTP
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); // Ativar se o servidor suportar TLS

        // Autenticação do e-mail remetente
        Session session = Session.getInstance(prop, new jakarta.mail.Authenticator() {
            protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                return new jakarta.mail.PasswordAuthentication("authpoogrupog2@gmail.com", "lnij nibn xkrm xbbu");
            }
        });

        // Criação da mensagem de e-mail
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("seuemail@seuservidor.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        message.setSubject(assunto);
        message.setText(conteudo);

        // Enviar a mensagem
        Transport.send(message);
    }
}

