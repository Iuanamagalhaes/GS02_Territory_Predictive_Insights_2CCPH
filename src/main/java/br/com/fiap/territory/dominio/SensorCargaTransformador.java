package br.com.fiap.territory.dominio;

import java.util.Random;

public class SensorCargaTransformador extends SensorSimulado implements Sensor {

    public SensorCargaTransformador(String id, String nome, String regiao) {
        this(id, nome, regiao, new Random());
    }

    public SensorCargaTransformador(String id, String nome, String regiao, Random random) {
        super(id, nome, regiao, TipoSensor.CARGA_TRANSFORMADOR, 62.0, 20.0, 125.0, 6.0, random);
    }
}
