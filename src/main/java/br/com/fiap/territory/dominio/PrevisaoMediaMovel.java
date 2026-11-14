package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;
import java.util.List;

public class PrevisaoMediaMovel extends EstrategiaPrevisao {
    private final int janela;

    public PrevisaoMediaMovel(int janela, int minimoLeituras) {
        super("Média móvel", minimoLeituras);
        this.janela = Math.max(1, janela);
    }

    @Override
    public String descrever() {
        return "média das últimas " + janela + " leituras do histórico";
    }

    @Override
    public double prever(List<Double> historico) throws DadosInvalidosException {
        super.prever(historico);
        int inicio = Math.max(0, historico.size() - janela);
        return media(historico.subList(inicio, historico.size()));
    }
}
