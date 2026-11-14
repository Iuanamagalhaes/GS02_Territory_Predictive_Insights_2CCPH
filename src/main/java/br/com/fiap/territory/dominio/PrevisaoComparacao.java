package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;

import java.util.List;

public class PrevisaoComparacao extends EstrategiaPrevisao {

    private final int janela;

    public PrevisaoComparacao(int janela, int minimoLeituras) {
        super("Comparação com período anterior", Math.max(2, minimoLeituras));
        this.janela = Math.max(1, janela);
    }

    @Override
    public String descrever() {
        return "média recente + tendência (média recente - média do período anterior), "
                + "usando até " + (janela * 2) + " leituras";
    }

    @Override
    public double prever(List<Double> historico) throws DadosInvalidosException {
        super.prever(historico);
        int usadas = Math.min(historico.size(), janela * 2);
        List<Double> recorte = historico.subList(historico.size() - usadas, historico.size());
        int meio = recorte.size() / 2;
        double mediaAnterior = media(recorte.subList(0, meio));
        double mediaRecente = media(recorte.subList(meio, recorte.size()));
        double tendencia = mediaRecente - mediaAnterior;
        return mediaRecente + tendencia;
    }
}
