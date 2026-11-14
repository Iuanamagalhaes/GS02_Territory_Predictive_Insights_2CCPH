package br.com.fiap.territory.dominio;

import java.util.Random;

public class SensorConsumoEnergia extends SensorSimulado implements Sensor {

    public SensorConsumoEnergia(String id, String nome, String regiao) {
        this(id, nome, regiao, new Random());
    }

    public SensorConsumoEnergia(String id, String nome, String regiao, Random random) {
        super(id, nome, regiao, TipoSensor.CONSUMO_ENERGIA, 2200.0, 600.0, 4500.0, 250.0, random);
    }
}
