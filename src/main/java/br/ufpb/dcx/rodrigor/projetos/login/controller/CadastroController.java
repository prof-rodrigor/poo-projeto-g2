package br.ufpb.dcx.rodrigor.projetos.login.controller;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import br.ufpb.dcx.rodrigor.projetos.login.exceptions.InvalidEmailException;
import br.ufpb.dcx.rodrigor.projetos.login.exceptions.InvalidPasswordException;
import br.ufpb.dcx.rodrigor.projetos.login.exceptions.InvalidUsernameException;
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

        if (isValidUsername(username) && isValidEmail(email) && isValidPassword(password)) {
            try {
                service.cadastrarNovoUsuario(usuario);
                System.out.println("Usuário "+ usuario.getUsername()+" cadastrado com sucesso");
                ctx.redirect("/login");
            } catch (InvalidEmailException IAE) {
                System.out.println(IAE.getMessage());
                ctx.redirect("/cadastro");
            } catch (InvalidUsernameException IUE) {
                System.out.println(IUE.getMessage());
                ctx.redirect("/cadastro");
            }
        } else {
            ctx.redirect("/cadastro");
        }
    }



    public boolean isValidUsername(String username) {
        return username != null && username.length() <= 12 && !username.contains(" ") && !containsSinal(username);
    }
    public boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.indexOf("@") < email.lastIndexOf(".") && email.length() <= 45;
    }
    public boolean isValidPassword(String password) {
        return password != null && !password.trim().isEmpty() && password.length() <= 20 && containsSinal(password);
    }

    public boolean containsSinal(String verify){
        for (int i = 0; i< verify.length(); i++){}
        return true;
    }



}

// TODO
/*
 * isValidUsername() : Validar username
 * isValidEmail(): Validar email
 * isValidPassword(): Validar senha
 * Implementar a telinha de erro, tipo, na hora de criar aparecer que nome,senha ou email foi no formato inválido ou já existe
 * Botão de redirecionar para tela de login pelo botão da tela de login em cadastro
 * Implementar JWT
 */

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