package br.ufpb.dcx.rodrigor.projetos.login.controller;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import br.ufpb.dcx.rodrigor.projetos.login.exceptions.InvalidEmailException;
import br.ufpb.dcx.rodrigor.projetos.login.exceptions.InvalidPasswordException;
import br.ufpb.dcx.rodrigor.projetos.login.exceptions.InvalidUsernameException;
import br.ufpb.dcx.rodrigor.projetos.login.model.Usuario;
import br.ufpb.dcx.rodrigor.projetos.login.service.UsuarioService;
import io.javalin.http.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

public class CadastroController {

    private static final Logger logger = LogManager.getLogger();
    public void rederizarCasdastro(Context ctx){
        ctx.render("registro/registro.html");
    }

    public void cadastrarUsuario(Context ctx){
        UsuarioService service = ctx.appData(Keys.USUARIO_SERVICE.key());
        String username = ctx.formParam("nome");
        String email = ctx.formParam("email");
        String password = ctx.formParam("senha");
        String hashedPassword = BCrypt.hashpw(password,BCrypt.gensalt(12));


        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setSenha(hashedPassword);

        if (isValidUsername(username) && isValidEmail(email) && isValidPassword(password)) {
            try {
                service.cadastrarNovoUsuario(usuario);
                ctx.redirect("/login");
            } catch (InvalidEmailException IAE) {
                ctx.redirect("/cadastro");
            } catch (InvalidUsernameException IUE) {
                ctx.redirect("/cadastro");
            }
        } else {
            ctx.redirect("/cadastro");
        }
    }



    public boolean isValidUsername(String username) {
        return username != null && username.length() <= 12 && !username.contains(" ") && !username.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    }
    public boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.indexOf("@") < email.lastIndexOf(".") && email.length() <= 20;
    }
    public boolean isValidPassword(String password) {
        return password != null && !password.trim().isEmpty() && password.length() <= 20 && !password.contains(" ");
    }

    public boolean containsSinal(String verify) {
        for (int i = 0; i < verify.length(); i++) {
            char verifica = verify.charAt(i);
            // 2A@
            if (!Character.isAlphabetic(verifica) && !Character.isDigit(verifica))
                return false;
        }
        System.out.println(verify);
        return true;
    }
}

