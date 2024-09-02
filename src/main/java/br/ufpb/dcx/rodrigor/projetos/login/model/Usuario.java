package br.ufpb.dcx.rodrigor.projetos.login.model;

import java.util.UUID;

public class Usuario {
    private String username;
    private String email;
    private String senha;
    private Cargo cargo;

    public Usuario(String username, String email, String senha, Cargo cargo) {
        this.username = username;
        this.email = email;
        this.senha = senha;
        this.cargo = cargo;
    }


    public Usuario() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }
}