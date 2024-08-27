package br.ufpb.dcx.rodrigor.projetos.login;

public class UsuarioVO {
    private String login;
    private String name;
    private String senha;

    public UsuarioVO(String email, String name, String senha) {
        this.login = email;
        this.name = name;
        this.senha = senha;
    }

    public UsuarioVO() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String email) {
        this.login = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
