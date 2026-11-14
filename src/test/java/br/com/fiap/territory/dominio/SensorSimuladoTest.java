package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.FonteIndisponivelException;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SensorSimuladoTest {

    @Test
    void leituraSimuladaFicaDentroDaFaixaValidaDoSensor() throws FonteIndisponivelException {
        Sensor sensor = new SensorTemperatura("TEMP-01", "Termometro", "Zona Teste", new Random(42));
        for (int i = 0; i < 200; i++) {
            double valor = sensor.lerValor();
            assertTrue(sensor.getTipo().estaNaFaixa(valor), "valor fora da faixa: " + valor);
        }
    }

    @Test
    void sensorComFalhaLancaFonteIndisponivel() {
        Sensor sensor = new SensorConsumoEnergia("ENERGIA-01", "Medidor", "Zona Teste", new Random(1));
        sensor.simularFalha(true);
        FonteIndisponivelException erro = assertThrows(FonteIndisponivelException.class, sensor::lerValor);
        assertTrue(erro.getMessage().contains("ENERGIA-01"));
        assertEquals(StatusComponente.INDISPONIVEL, sensor.verificarStatus());
    }

    @Test
    void sensorRecuperadoVoltaAOperar() throws FonteIndisponivelException {
        Sensor sensor = new SensorCargaTransformador("CARGA-01", "Transformador", "Zona Teste", new Random(7));
        sensor.simularFalha(true);
        sensor.simularFalha(false);
        double valor = sensor.lerValor();
        assertEquals(StatusComponente.OPERANDO, sensor.verificarStatus());
        assertTrue(sensor.getTipo().estaNaFaixa(valor));
    }
}
