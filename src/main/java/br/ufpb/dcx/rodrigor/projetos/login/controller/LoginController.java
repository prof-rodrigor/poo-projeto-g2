package br.ufpb.dcx.rodrigor.projetos.login.controller;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import br.ufpb.dcx.rodrigor.projetos.login.model.Usuario;
import br.ufpb.dcx.rodrigor.projetos.login.service.UsuarioService;
import io.javalin.http.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;


public class LoginController {
    private static final Logger logger = LogManager.getLogger(LoginController.class);

    public void mostrarPaginaLogin(Context ctx) {

        String teste = ctx.queryParam("teste");
        if(teste != null){
            throw new RuntimeException("Erro de teste a partir do /login?teste=1");
        }
        ctx.render("login.html");
    }

    public void processarLogin(Context ctx) {
        UsuarioService usuarioService = ctx.appData(Keys.USUARIO_SERVICE.key());
        String login = ctx.formParam("login");
        String senha = ctx.formParam("senha");
        Usuario usuario = usuarioService.getUsuario(login);
        if(usuario != null && BCrypt.checkpw(senha,usuario.getSenha())){
            ctx.sessionAttribute("usuario", usuario);
            logger.info("Usuário '{}' autenticado com sucesso.", login);
            ctx.redirect("/area-interna");
        }else {
            logger.warn("Tentativa de login falhou para o usuário: {}", login);
            ctx.redirect("/login");
        }
    }

    public void logout(Context ctx) {
        ctx.sessionAttribute("usuario", null);
        ctx.redirect("/login");
    }

    public void autenticar(Context ctx) {
        UsuarioService usuarioService = ctx.appData(Keys.USUARIO_SERVICE.key());

        Map<String, String> jsonMap = ctx.bodyAsClass(Map.class);
        String login = jsonMap.get("login");
        String senha = jsonMap.get("senha");
        if ( usuarioService.getUsuario(login) != null){
             Usuario usuario = usuarioService.getUsuario(login);
            if(BCrypt.checkpw(senha,usuario.getSenha())){
                Map<String, String> userResponse = new HashMap<>();
                userResponse.put("username", usuario.getUsername());
                userResponse.put("email", usuario.getEmail());
                userResponse.put("cargo", "Admin");

                ctx.json(userResponse);
                ctx.status(200);
            }
        }else {
            ctx.status(401).json(Map.of("error", "Usuário ou senha incorretos"));
        }

    }
}


















//if ( usuarioService.getUsuario(login) != null){
//Usuario usuario = usuarioService.getUsuario(login);
//            if(BCrypt.checkpw(senha,usuario.getSenha())){
//Map<String, String> userResponse = new HashMap<>();
//                userResponse.put("username", usuario.getUsername());
//        userResponse.put("email", usuario.getEmail());
//        userResponse.put("cargo", "Admin");
//
//                ctx.json(userResponse);
//                ctx.status(200);
//            }else {
//                    ctx.status(401).json(Map.of("error", "Usuário ou senha incorretos"));
//        }
//        }



