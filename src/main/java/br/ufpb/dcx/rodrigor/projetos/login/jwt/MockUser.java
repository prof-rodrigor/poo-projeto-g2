package br.ufpb.dcx.rodrigor.projetos.login.jwt;

import br.ufpb.dcx.rodrigor.projetos.login.model.Cargo;

public class MockUser {
    public String name;
    public Cargo level;

    public MockUser(String name, Cargo level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Cargo getLevel() {
        return level;
    }

    public void setLevel(Cargo level) {
        this.level = level;
    }
}
