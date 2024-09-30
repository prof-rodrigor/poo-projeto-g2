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

    public void editarPerfil(Context ctx) {
        UsuarioService usuarioService = ctx.appData(Keys.USUARIO_SERVICE.key());

        String nome = ctx.formParam("nome");
        String password = ctx.formParam("senha");

        logger.info("Dados recebidos - Nome: {}, Senha: {}", nome, password);

        if (!isValidUsername(nome)) {
            if (nome.isEmpty() || nome.equals(null)) {
                ctx.attribute("errorMessage", "Nome de usuario não pode ser nulo");
            } else if (nome.contains(" ")) {
                ctx.attribute("errorMessage", "Nome de usuario não pode conter espaços");//OK
            } else if (nome.matches(".[!@#$%^&()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
                ctx.attribute("errorMessage", "Nome de usuario não pode conter caracteres especiais"); //OK
            } else {
                ctx.attribute("errorMessage", "Nome de usuário inválido.");
            }
            ctx.render("perfil/editar_perfil.html");
        }


        if (!isValidPassword(password)) {
            if (password == null || password.isEmpty()) {
                ctx.attribute("errorMessage", "Senha inválida. Sua senha não pode ser vazia.");
            } else if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
                ctx.attribute("errorMessage", "Sua senha deve conter algum caractere especial: !, @, #, $, %, entre outros.");
            } else if (!password.matches(".*[A-Z].*")) {
                ctx.attribute("errorMessage", "Sua senha deve conter pelo menos uma letra MAIÚSCULA.");
            } else if (password.length() > 20 || password.length() < 4) {
                ctx.attribute("errorMessage", "Senha inválida. Deve ter entre 4 e 20 caracteres.");
            } else if (password.contains(" ")) {
                ctx.attribute("errorMessage", "Senha inválida. Não deve conter espaços.");
            }
            ctx.render("perfil/editar_perfil.html");



        }

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


