package br.ufpb.dcx.rodrigor.projetos.login.service;

import br.ufpb.dcx.rodrigor.projetos.App;
import br.ufpb.dcx.rodrigor.projetos.login.controller.PerfilController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class SendEmailSSL {
    private static final String PROP_EMAIL_USERNAME = "mail.username";
    private static final String PROP_EMAIL_PASSWORD = "mail.password";
    private static final Logger logger = LogManager.getLogger(PerfilController.class);
    Properties prop;

    public SendEmailSSL() throws MessagingException {
        prop = new Properties();
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("application.properties")) {
            if(input == null){
                logger.error("Arquivo de propriedades /src/main/resources/application.properties não encontrado");
                logger.error("Use o arquivo application.properties.examplo como base para criar o arquivo application.properties");
                System.exit(1);
            }
            prop.load(input);
        } catch (IOException ex) {
            logger.error("Erro ao carregar o arquivo de propriedades /src/main/resources/application.properties", ex);
            System.exit(1);
        }
    }

    public void sendEmail(String email, String subject, String content) {
        String username = prop.getProperty(PROP_EMAIL_USERNAME);
        String password = prop.getProperty(PROP_EMAIL_PASSWORD);
        logger.info("Lendo string de conexão ao MongoDB a partir do application.properties");
        if (username == null || password == null) {
            logger.error("A credenciais de email não foram definidas em  /src/main/resources/application.properties");
            System.exit(1);
        }

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        session.setDebug(true);
        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(PROP_EMAIL_USERNAME));

            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(email)
            );
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);

            System.out.println("Email enviado com sucesso!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
