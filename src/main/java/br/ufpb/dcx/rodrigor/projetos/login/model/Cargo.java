package br.ufpb.dcx.rodrigor.projetos.login.model;
import io.javalin.security.RouteRole;

import java.util.HashMap;
import java.util.Map;

public enum Cargo implements RouteRole {
    COORDENADOR,USUARIO;

    Map<String, Cargo> rolesMapping = new HashMap<String, Cargo>() {{
        put("Usuario", Cargo.USUARIO);
        put("Coordenador", Cargo.COORDENADOR);
    }};

}
