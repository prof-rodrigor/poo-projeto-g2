package br.ufpb.dcx.rodrigor.projetos;

import br.ufpb.dcx.rodrigor.projetos.db.MongoDBConnector;
import br.ufpb.dcx.rodrigor.projetos.login.controller.CadastroController;
import br.ufpb.dcx.rodrigor.projetos.login.controller.LoginController;
import br.ufpb.dcx.rodrigor.projetos.login.jwt.MockUser;
import br.ufpb.dcx.rodrigor.projetos.login.model.Cargo;
import br.ufpb.dcx.rodrigor.projetos.login.service.UsuarioService;
import br.ufpb.dcx.rodrigor.projetos.participante.controllers.ParticipanteController;
import br.ufpb.dcx.rodrigor.projetos.participante.services.ParticipanteService;
import br.ufpb.dcx.rodrigor.projetos.projeto.controllers.ProjetoController;
import br.ufpb.dcx.rodrigor.projetos.projeto.services.ProjetoService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;
import io.javalin.security.RouteRole;
import javalinjwt.JWTAccessManager;
import javalinjwt.JWTGenerator;
import javalinjwt.JWTProvider;
import javalinjwt.JavalinJWT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

public class App {
    private static final Logger logger = LogManager.getLogger();

    private static final int PORTA_PADRAO = 8000;
    private static final String PROP_PORTA_SERVIDOR = "porta.servidor";
    private static final String PROP_MONGODB_CONNECTION_STRING = "mongodb.connectionString";

    private final Properties propriedades;
    private MongoDBConnector mongoDBConnector = null;

    private JWTProvider provider;

    public App() {
        this.propriedades = carregarPropriedades();
    }



    public void iniciar() {
        Javalin app = inicializarJavalin();
        configurarPaginasDeErro(app);
        configurarRotas(app);
        // Lidando com exceções não tratadas
        app.exception(Exception.class, (e, ctx) -> {
            logger.error("Erro não tratado", e);
            ctx.status(500);
        });
    }

    private void registrarServicos(JavalinConfig config, MongoDBConnector mongoDBConnector) {
        ParticipanteService participanteService = new ParticipanteService(mongoDBConnector);
        config.appData(Keys.USUARIO_SERVICE.key(), new UsuarioService(mongoDBConnector));
        config.appData(Keys.PROJETO_SERVICE.key(), new ProjetoService(mongoDBConnector, participanteService));
        config.appData(Keys.PARTICIPANTE_SERVICE.key(), participanteService);
    }
    private void configurarPaginasDeErro(Javalin app) {
        app.error(404, ctx -> ctx.render("erro_404.html"));
        app.error(500, ctx -> ctx.render("erro_500.html"));
    }




    private Javalin inicializarJavalin() {
        int porta = obterPortaServidor();

        logger.info("Iniciando aplicação na porta {}", porta);

        Consumer<JavalinConfig> configConsumer = this::configureJavalin;


        //FIXME Implementação JWT  {
        Algorithm algorithm = Algorithm.HMAC256("Programacao-Orientada-a-Objetos");
        JWTGenerator<MockUser> generator = (user, alg) -> {
            JWTCreator.Builder token = JWT.create()
                    .withClaim("name", user.name)
                    .withClaim("level", user.level.ordinal());
            return token.sign(alg);
        };

        JWTVerifier verifier = JWT.require(algorithm).build();
        provider = new JWTProvider(algorithm, generator, verifier);

        Handler decodeHandler = JavalinJWT.createHeaderDecodeHandler(provider);

        Map<String, RouteRole> rolesMapping = new HashMap<>();
        rolesMapping.put("user", Cargo.USUARIO);
        rolesMapping.put("coordinator", Cargo.COORDENADOR);

        // }FIXME Implementação JWT

        // Inicializar o Javalin com a configuração e middleware


        return Javalin.create(configConsumer)
                .start(porta)
                .before(decodeHandler)
                .beforeMatched(ctx -> {
                    var userRole = getUserRole(ctx); // some user defined function that returns a user role
                    if (!ctx.routeRoles().contains(userRole)) { // routeRoles are provided through the Context interface
                        System.out.println("...");; // request will have to be explicitly stopped by throwing an exception
                    }
                });
    }

    private static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Cargo getUserRole(Context ctx) {
        String token = ctx.header("Authorization");
        if (token != null && validateToken(token)) {
            return Cargo.USUARIO;
        }
        return Cargo.USUARIO;
    }



    private void configureJavalin(JavalinConfig config) {
        TemplateEngine templateEngine = configurarThymeleaf();

        config.events(event -> {
            event.serverStarting(() -> {
                mongoDBConnector = inicializarMongoDB();
                config.appData(Keys.MONGO_DB.key(), mongoDBConnector);
                registrarServicos(config, mongoDBConnector);
            });
            event.serverStopping(() -> {
                if (mongoDBConnector == null) {
                    logger.error("MongoDBConnector não deveria ser nulo ao parar o servidor");
                } else {
                    mongoDBConnector.close();
                    logger.info("Conexão com o MongoDB encerrada com sucesso");
                }
            });
        });
        config.staticFiles.add(staticFileConfig -> {
            staticFileConfig.directory = "/public";
            staticFileConfig.location = Location.CLASSPATH;
        });
        config.fileRenderer(new JavalinThymeleaf(templateEngine));

    }

    private int obterPortaServidor() {
        if (propriedades.containsKey(PROP_PORTA_SERVIDOR)) {
            try {
                return Integer.parseInt(propriedades.getProperty(PROP_PORTA_SERVIDOR));
            } catch (NumberFormatException e) {
                logger.error("Porta definida no arquivo de propriedades não é um número válido: '{}'", propriedades.getProperty(PROP_PORTA_SERVIDOR));
                System.exit(1);
            }
        } else {
            logger.info("Porta não definida no arquivo de propriedades, utilizando porta padrão {}", PORTA_PADRAO);
        }
        return PORTA_PADRAO;
    }

    private TemplateEngine configurarThymeleaf() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }

    private MongoDBConnector inicializarMongoDB() {
        String connectionString = propriedades.getProperty(PROP_MONGODB_CONNECTION_STRING);
        logger.info("Lendo string de conexão ao MongoDB a partir do application.properties");
        if (connectionString == null) {
            logger.error("O string de conexão ao MongoDB não foi definido no arquivo /src/main/resources/application.properties");
            logger.error("Defina a propriedade '{}' no arquivo de propriedades", PROP_MONGODB_CONNECTION_STRING);
            System.exit(1);
        }

        logger.info("Conectando ao MongoDB");
        MongoDBConnector db = new MongoDBConnector(connectionString);
        if (db.conectado("config")) {
            logger.info("Conexão com o MongoDB estabelecida com sucesso");
        } else {
            logger.error("Falha ao conectar ao MongoDB");
            System.exit(1);
        }
        return db;
    }
    private void configurarRotas(Javalin app) {
        // Rotas protegidas com JWT e roles

        // Rota para gerar o token JWT
        app.get("/generate", context -> {
            MockUser mockUser = new MockUser("username", Cargo.USUARIO);
            String token = provider.generateToken(mockUser);
            context.json(new JWTResponse(token));
        }, RouteRole(Cargo.USUARIO)); // Essa rota pode ser acessada por qualquer um

        // Rota para validar o token JWT
        app.get("/validate", context -> {
            DecodedJWT decodedJWT = JavalinJWT.getDecodedFromContext(context);
            context.result("Hi " + decodedJWT.getClaim("name").asString());
        }, RouteRole(Cargo.USUARIO, Cargo.COORDENADOR)); // Usuários autenticados com roles USER ou COORDINATOR

        // Rota que apenas coordenadores podem acessar
        app.get("/coordinators", context -> {
            context.result("Área restrita para Coordenadores");
        }, RouteRole(Cargo.COORDENADOR)); // Apenas coordenadores

        // Rotas existentes com roles aplicadas
        CadastroController cadastroController = new CadastroController();
        app.get("/cadastro", cadastroController::rederizarCasdastro, RouteRole(Cargo.USUARIO));
        app.post("/cadastro", cadastroController::cadastrarUsuario, RouteRole(Cargo.USUARIO));

        LoginController loginController = new LoginController();
        app.get("/", ctx -> ctx.redirect("/login"), RouteRole(Cargo.USUARIO));
        app.get("/login", loginController::mostrarPaginaLogin, RouteRole(Cargo.USUARIO));
        app.post("/login", loginController::processarLogin, RouteRole(Cargo.USUARIO));
        app.get("/logout", loginController::logout, RouteRole(Cargo.USUARIO, Cargo.COORDENADOR));

        // Área interna restrita a usuários autenticados
        app.get("/area-interna", ctx -> {
            if (ctx.sessionAttribute("usuario") == null) {
                ctx.redirect("/login");
            } else {
                ctx.render("area_interna.html");
            }
        }, RouteRole(Cargo.USUARIO, Cargo.COORDENADOR));

        ProjetoController projetoController = new ProjetoController();
        app.get("/projetos", projetoController::listarProjetos, RouteRole(Cargo.USUARIO, Cargo.COORDENADOR));
        app.get("/projetos/novo", projetoController::mostrarFormulario, RouteRole(Cargo.COORDENADOR));
        app.post("/projetos", projetoController::adicionarProjeto, RouteRole(Cargo.COORDENADOR));
        app.get("/projetos/{id}/remover", projetoController::removerProjeto, RouteRole(Cargo.COORDENADOR));

        ParticipanteController participanteController = new ParticipanteController();
        app.get("/participantes", participanteController::listarParticipantes, RouteRole(Cargo.USUARIO, Cargo.COORDENADOR));
        app.get("/participantes/novo", participanteController::mostrarFormularioCadastro, RouteRole(Cargo.COORDENADOR));
        app.post("/participantes", participanteController::adicionarParticipante, RouteRole(Cargo.COORDENADOR));
        app.get("/participantes/{id}/remover", participanteController::removerParticipante, RouteRole(Cargo.COORDENADOR));
    }


//    Testando...
//    private void configurarRotas(Javalin app) {
//
//        CadastroController cadastroController = new CadastroController();
//        app.get("/cadastro",cadastroController::rederizarCasdastro);
//        app.post("/cadastro",cadastroController::cadastrarUsuario);
//
//
//        LoginController loginController = new LoginController();
//        app.get("/", ctx -> ctx.redirect("/login"));
//        app.get("/login", loginController::mostrarPaginaLogin);
//        app.post("/login", loginController::processarLogin);
//        app.get("/logout", loginController::logout);
//
//        app.get("/area-interna", ctx -> {
//            if (ctx.sessionAttribute("usuario") == null) {
//                ctx.redirect("/login");
//            } else {
//                ctx.render("area_interna.html");
//            }
//        });
//
//        ProjetoController projetoController = new ProjetoController();
//        app.get("/projetos", projetoController::listarProjetos);
//        app.get("/projetos/novo", projetoController::mostrarFormulario);
//        app.post("/projetos", projetoController::adicionarProjeto);
//        app.get("/projetos/{id}/remover", projetoController::removerProjeto);
//
//        ParticipanteController participanteController = new ParticipanteController();
//        app.get("/participantes", participanteController::listarParticipantes);
//        app.get("/participantes/novo", participanteController::mostrarFormularioCadastro);
//        app.post("/participantes", participanteController::adicionarParticipante);
//        app.get("/participantes/{id}/remover", participanteController::removerParticipante);
//
//    }

    private Properties carregarPropriedades() {
        Properties prop = new Properties();
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("application.properties")) {
            if(input == null){
                logger.error("Arquivo de propriedades /src/main/resources/application.properties não encontrado");
                logger.error("Use o arquivo application.properties.examplo como base para criar o arquivo application.properties");
                System.exit(1);
            }
            prop.load(input);
        } catch (IOException ex) {
            logger.error("Erro ao carregar o arquivo de propriedades /src/main/resources/application.properties", ex);
            System.exit(1);
        }
        return prop;
    }


    public static void main(String[] args) {
        try {
            new App().iniciar();

        } catch (Exception e) {
            logger.error("Erro ao iniciar a aplicação", e);
            System.exit(1);
        }
    }
}