package br.ufpb.dcx.rodrigor.projetos.login;

public class UsuarioService {

    public void cadastrarNovoUsuario(Usuario usuario){


    }

    public UsuarioVO getUsuario(String login){
        return null;

    }

    public Usuario voToUsuario(UsuarioVO vo){
        Usuario user = new Usuario();
        user.setLogin(vo.getLogin());
        user.setNome(vo.getName());
        user.setSenha(vo.getSenha());
        return user;
    }

    public UsuarioVO usuarioToVO(Usuario usuario){
        UsuarioVO vo = new UsuarioVO();
        vo.setLogin(usuario.getLogin());
        vo.setName(usuario.getNome());
        vo.setSenha(usuario.getSenha());
        return vo;
    }


}
