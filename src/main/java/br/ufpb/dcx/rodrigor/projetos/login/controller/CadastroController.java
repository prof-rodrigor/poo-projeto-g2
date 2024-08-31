package br.ufpb.dcx.rodrigor.projetos.login.controller;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import br.ufpb.dcx.rodrigor.projetos.login.model.Usuario;
import br.ufpb.dcx.rodrigor.projetos.login.service.UsuarioService;
import io.javalin.http.Context;
import org.mindrot.jbcrypt.BCrypt;

public class CadastroController {

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

        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("password: " + password);
        System.out.println("hashedpassword: " + hashedPassword);

        try{
            service.cadastrarNovoUsuario(usuario);
            System.out.println("ttt");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        ctx.redirect("/login");
    }

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