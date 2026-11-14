package br.com.fiap.territory.dominio;

public abstract class ComponenteTerritorial {

    private final String id;
    private String nome;
    private String regiao;
    private StatusComponente status = StatusComponente.DESCONHECIDO;

    protected ComponenteTerritorial(String id, String nome, String regiao) {
        if (id == null || id.isBlank() || nome == null || nome.isBlank()
                || regiao == null || regiao.isBlank()) {
            throw new IllegalArgumentException("id, nome e região do componente sao obrigatórios.");
        }
        this.id = id.trim();
        this.nome = nome.trim();
        this.regiao = regiao.trim();
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public StatusComponente getStatus() {
        return status;
    }

    protected void setStatus(StatusComponente status) {
        this.status = status;
    }

    public abstract StatusComponente verificarStatus();

    @Override
    public String toString() {
        return id + " - " + nome + " (" + regiao + ", " + status + ")";
    }
}