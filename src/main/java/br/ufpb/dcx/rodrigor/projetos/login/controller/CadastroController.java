package br.ufpb.dcx.rodrigor.projetos.login.controller;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import br.ufpb.dcx.rodrigor.projetos.login.model.Usuario;
import br.ufpb.dcx.rodrigor.projetos.login.service.UsuarioService;
import io.javalin.http.Context;

public class CadastroController {

    public void rederizarCasdastro(Context ctx){
        ctx.render("/login/cadastro.html");
    }

    public void cadastrarUsuario(Context ctx){
        UsuarioService service = ctx.appData(Keys.USUARIO_SERVICE.key());
        String login = ctx.pathParam("email");
        String name = ctx.pathParam("nome");
        String password = ctx.pathParam("password");
        Usuario usuario = new Usuario(login, name, password);
        try{
            service.cadastrarNovoUsuario(usuario);
        }catch (Exception e){}
        ctx.redirect("/login");
    }

    public void getUsuario(Context ctx){

    }

    public void editarUsuario(Context ctx){}


}

//       TODO implementar exceptions
//        catch(UsernameAlreadyExistsException UAEE){
//            //
//        }catch (InvalidEmailException IAE){
//            //
//        }catch (InvalidPasswordException IPE){
//            //
//        }catch (InvalidUsernameException IUE){
//            //
//        }catch (AuthorizationException AE){
//            // Se o usuário não tiver permissão para cadastrar um usuário (por exemplo, se o usuário não for administrador), você deve lançar essa exceção.
//        }