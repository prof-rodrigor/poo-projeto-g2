package br.ufpb.dcx.rodrigor.projetos.login.controller;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import br.ufpb.dcx.rodrigor.projetos.login.model.Usuario;
import br.ufpb.dcx.rodrigor.projetos.login.service.SendEmailSSL;
import br.ufpb.dcx.rodrigor.projetos.login.service.UsuarioService;
import io.javalin.http.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import javax.mail.MessagingException;
import java.util.Random;

public class EsqueciSenhaController {

    private static final Logger logger = LogManager.getLogger(PerfilController.class);


    public void mostrarPaginaEsqueciSenha(Context ctx) {

        String teste = ctx.queryParam("teste");
        if(teste != null){
            throw new RuntimeException("Erro de teste a partir do /login?teste=1");
        }
        ctx.render("/conta/esqueci-a-senha-email.html");
    }

    public void enviaCodigoRecuperacaoEmail(Context ctx){
        UsuarioService usuarioService = ctx.appData(Keys.USUARIO_SERVICE.key());
        String email = ctx.formParam("email");
        if (isValidEmail(email)){
            Usuario usuario = usuarioService.getUsuarioByEmail(email);
            if (usuario != null){
                try {
                    SendEmailSSL sendEmailSSL = new SendEmailSSL();
                    Random rand = new Random();
                    String code = String.valueOf(100000 + rand.nextInt(900000));
                    ctx.attribute("recoveryCode",code);
                    ctx.attribute("email",email);
                    String msg = "Código de recuperação: "+ code;
                    sendEmailSSL.sendEmail(email, " POOGERPRO - Recuperação de senha", msg);
                    ctx.render("/conta/esqueci-a-senha-codigo.html");
                }catch (MessagingException me){
                    // TODO Implementar caso email não exista/incorreto
                }
            }
        }else {
            // TODO Implementar caso email não seja válido
        }
    }

    public void alterarSenhaComCodigoRecuperacao(Context ctx){
        UsuarioService usuarioService = ctx.appData(Keys.USUARIO_SERVICE.key());
        String recoveryCode = ctx.attribute("recoveryCode");
        String codigo = ctx.formParam("codigo");
        if (codigo != null && codigo.equals(recoveryCode)){
            String senha = ctx.formParam("novasenha");
            String email = ctx.attribute("email") ;
            if (isValidPassword(senha)){
                Usuario usuario = usuarioService.getUsuarioByEmail(email);
                String hashedPassword = BCrypt.hashpw(senha, BCrypt.gensalt(12));
                usuario.setSenha(hashedPassword);
                usuarioService.atualizarUsuario(usuario);
                logger.info("Usuario"+ usuario.getUsername()+" atualizado com sucesso");
                ctx.redirect("/");
            }else{
                //TODO Exibir erro de nova senha inválida
            }
        }else{
            // TODO Exibir erro de código incorreto
        }
    }
    public boolean isValidPassword(String password) {
        return password != null && !password.trim().isEmpty() && password.length() <= 20 && password.length() >= 4
                && password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")
                && password.matches(".*[A-Z].*")
                && !password.contains(" ");
    }
    public boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.indexOf("@") < email.lastIndexOf(".") && email.length() <= 64;
    }

}

// Primeiro estágio, envia código para email, salva o código encriptado no dom]
// Redireciona para a proxima pagina
// Usuario insere código e a nova senha
