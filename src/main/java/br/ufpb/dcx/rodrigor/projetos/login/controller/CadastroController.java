package br.ufpb.dcx.rodrigor.projetos.login.controller;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import br.ufpb.dcx.rodrigor.projetos.login.exceptions.InvalidEmailException;
import br.ufpb.dcx.rodrigor.projetos.login.exceptions.InvalidPasswordException;
import br.ufpb.dcx.rodrigor.projetos.login.exceptions.InvalidUsernameException;
import br.ufpb.dcx.rodrigor.projetos.login.model.Usuario;
import br.ufpb.dcx.rodrigor.projetos.login.service.SendEmailSSL;
import br.ufpb.dcx.rodrigor.projetos.login.service.UsuarioService;
import io.javalin.http.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import javax.mail.MessagingException;

public class CadastroController {

    private static final Logger logger = LogManager.getLogger();
    public void rederizarCasdastro(Context ctx){
        ctx.render("registro/registro.html");
    }

    public void cadastrarUsuario(Context ctx) {
        UsuarioService service = ctx.appData(Keys.USUARIO_SERVICE.key());

        String username = ctx.formParam("nome");
        String email = ctx.formParam("email");
        String password = ctx.formParam("senha");
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setSenha(hashedPassword);

        if (isValidUsername(username) && isValidEmail(email) && isValidPassword(password)) {
            try {
                SendEmailSSL sendEmailSSL = new SendEmailSSL();
                service.cadastrarNovoUsuario(usuario);
                String subject = "GERPRO - Bem vindo ao GERPRO";
                String content = username+",sua conta foi criada com sucesso, agora você faz parte da GERPRO!";
                sendEmailSSL.sendEmail(usuario.getEmail(),subject,content);
                ctx.redirect("/login");
            } catch (InvalidEmailException iae) {
                ctx.attribute("errorMessage", "Este email já foi cadastrado.");
                ctx.render("registro/registro.html");
            } catch (InvalidUsernameException iue) {
                ctx.attribute("errorMessage", "Este nome de usuário já existe ou é inválido.");
                ctx.render("registro/registro.html");
            }catch (MessagingException me){
                //TODO
            }
        } else {
            if (!isValidUsername(username)) {
                if(username.isEmpty() || username.equals(null)){
                    ctx.attribute("errorMessage", "Nome de usuario não pode ser nulo");
                } else if (username.contains(" ")) {
                    ctx.attribute("errorMessage", "Nome de usuario não pode conter espaços");//OK
                }else if (username.matches(".[!@#$%^&()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")){
                    ctx.attribute("errorMessage", "Nome de usuario não pode conter caracteres especiais"); //OK
                }else{
                    ctx.attribute("errorMessage", "Nome de usuário inválido.");
                }
            } else if (!isValidEmail(email)) {
                ctx.attribute("errorMessage", "Email inválido.");
            } else if (!isValidPassword(password)) {
                if(!password.contains(".[!@#$%^&()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")){
                    ctx.attribute("errorMessage", "Sua senha deve conter algum caractere especial: !, @, #, $, %, entre outros");
                } else if (isUpperCase(password)) {
                    ctx.attribute("errorMessage", "Sua senha deve conter alguma letra MAIUSCULA");
                } else if (password.length() > 20 || password.length() < 4) {
                    ctx.attribute("errorMessage", "Senha inválida. Deve ter entre 4 e 20 caracteres e não pode conter espaços.");
                }else if(password != null || password.isEmpty()){
                    ctx.attribute("errorMessage", "Senha inválida. Sua senha não pode ser vazia.");
                } else if (!password.trim().isEmpty()) {
                    ctx.attribute("errorMessage", "Senha inválida. Sua senha não deve conter espaços.");
                }
            }

            ctx.render("registro/registro.html");
        }
    }


    public boolean isValidUsername(String username) {
        return username != null && username.length() <= 12 && !username.contains(" ") && !username.matches(".[!@#$%^&()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    }
    public boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.indexOf("@") < email.lastIndexOf(".") && email.length() <= 64;
    }
    public boolean isValidPassword(String password) {
        return password != null && !password.trim().isEmpty() && password.length() <= 20 && !password.contains(" ") && password.length()>= 4;
    }
    public boolean isUpperCase(String password){
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
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

