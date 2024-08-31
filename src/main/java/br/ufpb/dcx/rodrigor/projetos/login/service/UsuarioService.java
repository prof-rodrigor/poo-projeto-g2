package br.ufpb.dcx.rodrigor.projetos.login.service;

import br.ufpb.dcx.rodrigor.projetos.AbstractService;
import br.ufpb.dcx.rodrigor.projetos.db.MongoDBConnector;
import br.ufpb.dcx.rodrigor.projetos.login.model.Usuario;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.LinkedList;
import java.util.List;

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

    public List<Usuario> listarUsuarios(){
        List<Usuario> usuarios = new LinkedList<>();
        for (Document doc : repository.find()){
            usuarios.add(voToUser(doc));
        }
        return usuarios;
    }

    public Usuario getUsuario(String username) {
        Bson filter = Filters.eq("username", username);
        Document doc = repository.find(filter).first();

        return doc != null ? voToUser(doc) : null;
    }

    public Usuario voToUser(Document doc) {
        Usuario usuario = new Usuario();
        usuario.setUsername(doc.getString("username"));
        usuario.setEmail(doc.getString("email"));
        usuario.setSenha(doc.getString("senha"));
        return usuario;
    }
    public Document userToVO(Usuario usuario){
        Document vo = new Document();
        vo.put("username", usuario.getUsername());
        vo.put("email", usuario.getEmail());
        vo.put("senha", usuario.getSenha());
        return vo;
    }

}



