package br.com.fiap.territory.dominio;

import java.util.Random;

public class SensorTemperatura extends SensorSimulado implements Sensor {

    public SensorTemperatura(String id, String nome, String regiao) {
        this(id, nome, regiao, new Random());
    }

    public SensorTemperatura(String id, String nome, String regiao, Random random) {
        super(id, nome, regiao, TipoSensor.TEMPERATURA, 30.0, 18.0, 48.0, 2.0, random);
    }
}