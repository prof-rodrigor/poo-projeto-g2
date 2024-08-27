package br.ufpb.dcx.rodrigor.projetos.login;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import io.javalin.http.Context;

public class UsuarioController {

    public void rederizarCasdastro(Context ctx){
        ctx.render("/login/cadastro.html");
    }

    public void cadastrarUsuario(Context ctx){
        UsuarioService usuarioService = ctx.appData(Keys.USUARIO_SERVICE.key());
        String login = ctx.pathParam("email");
        String name = ctx.pathParam("nome");
        String password = ctx.pathParam("password");

    }

    public void editarUsuario(Context ctx){}
}
