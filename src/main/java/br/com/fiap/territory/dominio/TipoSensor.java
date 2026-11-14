package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;
import java.util.Locale;

public enum TipoSensor {
    TEMPERATURA("Temperatura ambiente", "C", -10.0, 60.0,
            "risco de onda de calor e aumento da demanda por refrigeração"),
    CARGA_TRANSFORMADOR("Carga do transformador", "%", 0.0, 150.0,
            "risco de sobrecarga e desligamento do transformador"),
    CONSUMO_ENERGIA("Consumo do alimentador", "kW", 0.0, 10000.0,
            "risco de sobrecarga na rede de distribuição");

    private final String descricao;
    private final String unidade;
    private final double minimo;
    private final double maximo;
    private final String risco;

    TipoSensor(String descricao, String unidade, double minimo, double maximo, String risco) {
        this.descricao = descricao;
        this.unidade = unidade;
        this.minimo = minimo;
        this.maximo = maximo;
        this.risco = risco;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getUnidade() {
        return unidade;
    }

    public double getMinimo() {
        return minimo;
    }

    public double getMaximo() {
        return maximo;
    }

    public String getRisco() {
        return risco;
    }

    public boolean estaNaFaixa(double valor) {
        return !Double.isNaN(valor) && !Double.isInfinite(valor) && valor >= minimo && valor <= maximo;
    }

    public void validar(double valor) throws DadosInvalidosException {
        if (!estaNaFaixa(valor)) {
            throw new DadosInvalidosException(String.format(Locale.US,
                    "Medição de %s fora da faixa válida (%.1f a %.1f %s): %s",
                    descricao.toLowerCase(Locale.ROOT), minimo, maximo, unidade, valor));
        }
    }
}
