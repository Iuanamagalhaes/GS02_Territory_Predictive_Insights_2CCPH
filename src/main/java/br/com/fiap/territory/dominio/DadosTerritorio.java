package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;
import java.util.EnumMap;
import java.util.Map;

public class DadosTerritorio {
    private String nome;
    private String regiao;
    private int populacao;
    private final Map<TipoSensor, Limiar> limiares = new EnumMap<>(TipoSensor.class);

    public DadosTerritorio(String nome, String regiao, int populacao) throws DadosInvalidosException {
        setNome(nome);
        setRegiao(regiao);
        setPopulacao(populacao);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) throws DadosInvalidosException {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome do território e obrigatório.");
        }
        this.nome = nome.trim();
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) throws DadosInvalidosException {
        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException("A região do território e obrigatória.");
        }
        this.regiao = regiao.trim();
    }

    public int getPopulacao() {
        return populacao;
    }

    public void setPopulacao(int populacao) throws DadosInvalidosException {
        if (populacao < 0) {
            throw new DadosInvalidosException("População não pode ser negativa: " + populacao);
        }
        this.populacao = populacao;
    }

    public Limiar getLimiar(TipoSensor tipo) {
        Limiar limiar = limiares.get(tipo);
        if (limiar == null) {
            throw new IllegalStateException("Limiar não configurado para " + tipo);
        }
        return limiar;
    }

    public void definirLimiar(TipoSensor tipo, double atencao, double alerta, double critico)
            throws DadosInvalidosException {
        limiares.put(tipo, new Limiar(tipo, atencao, alerta, critico));
    }
}
