package br.ufpb.dcx.rodrigor.projetos.login.service;

import br.ufpb.dcx.rodrigor.projetos.AbstractService;
import br.ufpb.dcx.rodrigor.projetos.db.MongoDBConnector;
import br.ufpb.dcx.rodrigor.projetos.login.model.Usuario;
import br.ufpb.dcx.rodrigor.projetos.participante.services.ParticipanteService;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;

import java.util.Objects;

public class UsuarioService extends AbstractService {
    private static final Logger logger = LogManager.getLogger();

    private final MongoCollection<Document> repository;

    public UsuarioService(MongoDBConnector mongoDBConnector) {
        super(mongoDBConnector);
        MongoDatabase database = mongoDBConnector.getDatabase("usuarios");
        this.repository = database.getCollection("usuarios");
    }


    public void cadastrarNovoUsuario(Usuario usuario){
        Document doc = userToVO(usuario);
        repository.insertOne(doc);
    }

    public Usuario getUsuario(String login){
        Bson filter = Filters.eq("login", login);
        FindIterable<Document> iterable = repository.find(filter);
        Usuario usuario = voToUser(iterable.first());
        if (usuario.getNome() == null || usuario.getNome().isEmpty()){
            return null;
        }
        return usuario;
    }

    public Usuario voToUser(Document doc) {
        Usuario usuario = new Usuario();
        usuario.setNome(doc.getString("nome"));
        usuario.setLogin(doc.getString("login"));
        usuario.setSenha(doc.getString("senha"));
        return usuario;
    }
    public Document userToVO(Usuario usuario){
        Document vo = new Document();

        vo.put("nome", usuario.getNome());
        vo.put("login", usuario.getLogin());
        vo.put("senha", usuario.getSenha());
        return vo;
    }


    // Implementar chacagem
//    private void checkUser(Usuario usuario) throws InvalidPasswordException, InvalidUsernameException, DatabaseException, AuthenticationException, UsernameAlreadyExistsException, InvalidEmailException, AuthorizationException {
//    if(test == 1) throw new UsernameAlreadyExistsException();
//    if(test == 2) throw new InvalidEmailException();
//    if(test == 3) throw new InvalidPasswordException();
//    if(test == 4) throw new InvalidUsernameException() ;
//    if(test == 5) throw new AuthorizationException();
//    }


}



