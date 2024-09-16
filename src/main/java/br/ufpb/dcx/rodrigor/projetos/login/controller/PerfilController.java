package br.ufpb.dcx.rodrigor.projetos.login.controller;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import br.ufpb.dcx.rodrigor.projetos.login.model.Usuario;
import br.ufpb.dcx.rodrigor.projetos.login.service.UsuarioService;
import io.javalin.http.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

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

            // Renderizar o formulário de edição
            ctx.render("perfil/editar_perfil.html");
            logger.info("Página de edição de perfil acessada pelo usuário '{}'", usuario.getUsername());
        } else {
            logger.warn("Usuário não autenticado tentou acessar a página de edição de perfil.");
            ctx.redirect("/login");
        }
    }

    public void editarPerfil(Context ctx) {
        UsuarioService usuarioService = ctx.appData(Keys.USUARIO_SERVICE.key());
        Usuario usuario = ctx.sessionAttribute("usuario");

        if (usuario == null) {
            logger.warn("Tentativa de editar perfil sem estar autenticado.");
            ctx.redirect("/login");
            return;
        }

        // Obtendo os parâmetros do formulário
        String nome = ctx.formParam("nome");
        String password = ctx.formParam("senha");

        // Log para verificar os dados recebidos
        logger.info("Dados recebidos - Nome: {}, Senha: {}", nome, password);

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

        // Atualizando os dados do usuário
        usuario.setUsername(nome);
        usuario.setSenha(hashedPassword);

        // Atualizando no banco de dados
        usuarioService.atualizarUsuario(usuario);

        // Redirecionar para a página de perfil após a atualização
        ctx.redirect("/login");
        logger.info("Perfil atualizado com sucesso: nome={}, email={}", usuario.getUsername(), usuario.getEmail());
    }

}
