package br.com.fiap.territory.dominio;

public enum NivelAlerta {
    NORMAL("sem risco previsto"),
    ATENCAO("monitorar com mais frequência e conferir a previsão nas próximas leituras"),
    ALERTA("preparar a equipe de campo e avisar a população do território"),
    CRITICO("avaliar o acionamento do plano de contingência (reforço da rede, pontos de resfriamento)");

    private final String orientacao;

    NivelAlerta(String orientacao) {
        this.orientacao = orientacao;
    }

    public String getOrientacao() {
        return orientacao;
    }
}

