package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;

import java.util.List;

public abstract class EstrategiaPrevisao {
    private final String nome;
    private final int minimoLeituras;

    protected EstrategiaPrevisao(String nome, int minimoLeituras) {
        this.nome = nome;
        this.minimoLeituras = minimoLeituras;
    }

    public String getNome() {
        return nome;
    }

    public int getMinimoLeituras() {
        return minimoLeituras;
    }

    public abstract String descrever();

    public double prever(List<Double> historico) throws DadosInvalidosException {
        int quantidade = historico == null ? 0 : historico.size();
        if (quantidade < minimoLeituras) {
            throw new DadosInvalidosException("Histórico insuficiente para a previsão '" + nome + "': "
                    + quantidade + " leitura(s) no banco, mínimo de " + minimoLeituras + ".");
        }
        return historico.get(quantidade - 1);
    }

    protected static double media(List<Double> valores) {
        double soma = 0;
        for (double v : valores) {
            soma += v;
        }
        return soma / valores.size();
    }
}