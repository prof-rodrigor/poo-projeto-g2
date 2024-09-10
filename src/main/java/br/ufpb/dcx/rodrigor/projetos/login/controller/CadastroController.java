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
                logger.info("Usuário {} cadastrado com sucesso", usuario);
                ctx.redirect("/login");
            } catch (InvalidEmailException iae) {
                logger.error(iae);
                ctx.redirect("/cadastro");
            } catch (InvalidUsernameException iue) {
                logger.error((iue));
                ctx.redirect("/cadastro");
            }
        } else {
            ctx.redirect("/cadastro");
        }
    }



    public boolean isValidUsername(String username) {
        return username != null && username.length() <= 12 && !username.contains(" ") && containsSinal(username);
    }
    public boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.indexOf("@") < email.lastIndexOf(".") ;
    }
    public boolean isValidPassword(String password) {
        return password != null && !password.trim().isEmpty() && password.length() <= 20;
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

    // 2 -> alfabetico ? não = Sim -> 2 é digito? sim = não  ----- > (Sim e Não ) = não
    // A --> Alfabetico ? Sim = não -> A é número? não = sim -----> (Não e Sim) = não
    // @ --> Alfabeitico ? não = Sim -> @ É número? não = Sim ----> (Sim e Sim) = Sim --> false,




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