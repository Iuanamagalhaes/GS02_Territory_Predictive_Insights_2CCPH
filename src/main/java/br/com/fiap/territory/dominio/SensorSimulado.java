package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.FonteIndisponivelException;
import java.util.Random;

public abstract class SensorSimulado extends ComponenteTerritorial implements Sensor {
    private final TipoSensor tipo;
    private final Random random;
    private final double minimoSimulado;
    private final double maximoSimulado;
    private final double passo;
    private double ultimoValor;
    private boolean falhaSimulada;

    protected SensorSimulado(String id, String nome, String regiao, TipoSensor tipo,
                             double valorInicial, double minimoSimulado, double maximoSimulado,
                             double passo, Random random) {
        super(id, nome, regiao);
        this.tipo = tipo;
        this.ultimoValor = valorInicial;
        this.minimoSimulado = minimoSimulado;
        this.maximoSimulado = maximoSimulado;
        this.passo = passo;
        this.random = random;
    }

    @Override
    public TipoSensor getTipo() {
        return tipo;
    }

    @Override
    public double lerValor() throws FonteIndisponivelException {
        if (!estaFuncionando()) {
            throw new FonteIndisponivelException(
                    "Sensor " + getId() + " (" + getNome() + ") indisponível: falha de comunicação.");
        }
        double variacao = (random.nextDouble() - 0.35) * passo;
        ultimoValor = Math.max(minimoSimulado, Math.min(maximoSimulado, ultimoValor + variacao));
        return Math.round(ultimoValor * 100.0) / 100.0;
    }

    @Override
    public boolean estaFuncionando() {
        return !falhaSimulada;
    }

    @Override
    public void simularFalha(boolean falha) {
        this.falhaSimulada = falha;
    }

    @Override
    public StatusComponente verificarStatus() {
        StatusComponente novo = estaFuncionando() ? StatusComponente.OPERANDO : StatusComponente.INDISPONIVEL;
        setStatus(novo);
        return novo;
    }
}