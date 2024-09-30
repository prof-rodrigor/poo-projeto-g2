package br.ufpb.dcx.rodrigor.projetos.login.controller;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import br.ufpb.dcx.rodrigor.projetos.login.service.EmailService;
import io.javalin.http.Context;
import br.ufpb.dcx.rodrigor.projetos.login.service.UsuarioService;
import org.apache.commons.mail.EmailException;

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
            String linkRecuperacao = "localhost:8000/new_password";
            //?token=" + token
            // Tenta enviar o e-mail com o link de recuperação
            try {
                emailService.enviarEmail(email, "Recuperação de Senha",
                        "Por favor, use o seguinte link para redefinir sua senha: " + linkRecuperacao);
                ctx.attribute("successMessage", "Um e-mail com instruções de recuperação foi enviado.");
                ctx.render("email_sent.html");
            } catch (EmailException e) {
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
//    public void exibirFormularioNovaSenha(Context ctx) {
//        // Captura o token da URL
//        String token = ctx.queryParam("token");
//
//        // Serviço de usuário que valida o token
//        UsuarioService usuarioService = ctx.appData(Keys.USUARIO_SERVICE.key());
//
//        // Valida se o token é válido e se não está expirado
//        if (usuarioService.isTokenValido(token)) {
//            // Token válido, renderiza o formulário de redefinição de senha
//            ctx.attribute("token", token); // Envia o token como atributo para o template
//            ctx.render("new_password_form.html"); // Renderiza o formulário
//        } else {
//            // Token inválido ou expirado, renderiza uma página de erro
//            ctx.attribute("errorMessage", "Token inválido ou expirado.");
//            ctx.render("error.html");
//        }
//    }
//    public void processarNovaSenha(Context ctx) {
//        // Captura o token e a nova senha do formulário
//        String token = ctx.formParam("token");
//        String novaSenha = ctx.formParam("password");
//
//        // Serviço de usuário que valida o token e atualiza a senha
//        UsuarioService usuarioService = ctx.appData(Keys.USUARIO_SERVICE.key());
//
//        // Valida o token novamente
//        if (usuarioService.isTokenValido(token)) {
//            // Atualiza a senha no banco de dados
//            usuarioService.atualizarSenha(token, novaSenha);
//            ctx.attribute("successMessage", "Senha atualizada com sucesso!");
//            ctx.render("success.html"); // Renderiza uma página de sucesso
//        } else {
//            // Token inválido ou expirado, renderiza uma página de erro
//            ctx.attribute("errorMessage", "Token inválido ou expirado.");
//            ctx.render("error.html");
//        }
//    }




}

//https://mailtrap.io/blog/java-send-email-gmail/
