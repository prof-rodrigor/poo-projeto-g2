package br.ufpb.dcx.rodrigor.projetos.login.controller;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import br.ufpb.dcx.rodrigor.projetos.login.service.EmailService;
import io.javalin.http.Context;
import br.ufpb.dcx.rodrigor.projetos.login.service.UsuarioService;
import jakarta.mail.MessagingException;

public class RecuperacaoSenhaController {

    private EmailService emailService = new EmailService(); // Instância do serviço de e-mail

    public void exibirFormularioRecuperacaoSenha(Context ctx) {
        ctx.render("recuperarsenha.html"); // Renderiza a página de recuperação de senha
    }

    public void enviarEmailRecuperacaoSenha(Context ctx) {
        UsuarioService usuarioService = ctx.appData(Keys.USUARIO_SERVICE.key());
        String email = ctx.formParam("email");

        // Verifica se o e-mail existe no banco de dados
        if (usuarioService.getUsuarioByEmail(email) != null) {
            String token = gerarTokenRecuperacao(); // Geração de token seguro
            String linkRecuperacao = "https://localhost:8000/new_password?token=" + token;

            // Tenta enviar o e-mail com o link de recuperação
            try {
                emailService.enviarEmail(email, "Recuperação de Senha", "Por favor, use o seguinte link para redefinir sua senha: " + linkRecuperacao);
                ctx.attribute("successMessage", "Um e-mail com instruções de recuperação foi enviado.");
                ctx.render("email_sent.html");
            } catch (MessagingException e) {
                ctx.attribute("errorMessage", "Erro ao enviar e-mail. Por favor, tente novamente.");
                ctx.render("recuperarsenha.html");
            }
        } else {
            ctx.attribute("errorMessage", "E-mail não encontrado.");
            ctx.render("recuperarsenha.html");
        }
    }

    private String gerarTokenRecuperacao() {
        return java.util.UUID.randomUUID().toString(); // Utiliza UUID para gerar um token simples
    }
}
