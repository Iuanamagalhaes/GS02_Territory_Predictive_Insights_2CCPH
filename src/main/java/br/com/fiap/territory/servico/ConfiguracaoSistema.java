package br.com.fiap.territory.servico;

import br.com.fiap.territory.dominio.DadosTerritorio;
import br.com.fiap.territory.dominio.TipoSensor;
import br.com.fiap.territory.excecao.DadosInvalidosException;

public final class ConfiguracaoSistema {
    private static ConfiguracaoSistema instancia;
    private DadosTerritorio dadosTerritorio;
    private int janelaMediaMovel;
    private int janelaComparacao;
    private int minimoLeituras;

    private ConfiguracaoSistema() {
        aplicarPadrao();
    }

    public static synchronized ConfiguracaoSistema getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracaoSistema();
        }
        return instancia;
    }

    public synchronized void resetarPadrao() {
        aplicarPadrao();
    }

    private void aplicarPadrao() {
        try {
            DadosTerritorio territorio = new DadosTerritorio("Vila Esperança", "Zona Leste", 18500);
            territorio.definirLimiar(TipoSensor.TEMPERATURA, 35.0, 38.0, 41.0);
            territorio.definirLimiar(TipoSensor.CARGA_TRANSFORMADOR, 70.0, 85.0, 100.0);
            territorio.definirLimiar(TipoSensor.CONSUMO_ENERGIA, 2800.0, 3300.0, 3800.0);
            this.dadosTerritorio = territorio;
        } catch (DadosInvalidosException e) {
            throw new IllegalStateException("Configuração padrão inválida: " + e.getMessage(), e);
        }
        this.janelaMediaMovel = 5;
        this.janelaComparacao = 3;
        this.minimoLeituras = 5;
    }

    public DadosTerritorio getDadosTerritorio() {
        return dadosTerritorio;
    }

    public int getJanelaMediaMovel() {
        return janelaMediaMovel;
    }

    public int getJanelaComparacao() {
        return janelaComparacao;
    }

    public int getMinimoLeituras() {
        return minimoLeituras;
    }
}