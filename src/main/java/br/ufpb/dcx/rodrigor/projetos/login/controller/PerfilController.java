package br.ufpb.dcx.rodrigor.projetos.login.controller;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import br.ufpb.dcx.rodrigor.projetos.login.exceptions.InvalidUsernameException;
import br.ufpb.dcx.rodrigor.projetos.login.model.Usuario;
import br.ufpb.dcx.rodrigor.projetos.login.service.UsuarioService;
import io.javalin.http.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class PerfilController {

    private static final Logger logger = LogManager.getLogger(PerfilController.class);

    public void mostrarPaginaEditarPerfil(Context ctx) {
        // Obter o serviço de usuário a partir do contexto da aplicação
        UsuarioService usuarioService = ctx.appData(Keys.USUARIO_SERVICE.key());
        Usuario usuario = ctx.sessionAttribute("usuario");

        if (usuario != null) {
            // Preencher atributos com informações do usuário logado
            ctx.attribute("nome", usuario.getUsername());
            ctx.attribute("email", usuario.getEmail());

            ctx.render("perfil/editar_perfil.html");
            logger.info("Página de edição de perfil acessada pelo usuário '{}'", usuario.getUsername());
        } else {
            logger.warn("Usuário não autenticado tentou acessar a página de edição de perfil.");
            ctx.redirect("/login");
        }
    }

    public void editarPerfil(Context ctx){
        UsuarioService usuarioService = ctx.appData(Keys.USUARIO_SERVICE.key());

        String nome = ctx.formParam("nome");
        String password = ctx.formParam("senha");

        logger.info("Dados recebidos - Nome: {}, Senha: {}", nome, password);

        Usuario usuario1 = new Usuario();
        usuario1.setUsername(nome);

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        usuario1.setSenha(hashedPassword);

        usuario1.setEmail(ctx.sessionAttribute("userEmail"));

        try {
            usuarioService.atualizarUsuario(usuario1);
            ctx.redirect("/login");
        } catch (NullPointerException e ){
            ctx.redirect("/login");
        }



    }
    public boolean isValidUsername(String username) {
        return username != null && username.length() <= 12 && !username.contains(" ")
                && !username.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    }
    public boolean isValidPassword(String password) {
        return password != null && !password.trim().isEmpty() && password.length() <= 20 && password.length() >= 4
                && password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")
                && password.matches(".*[A-Z].*")
                && !password.contains(" ");
    }
}


