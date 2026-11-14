package br.com.fiap.territory.servico;

import br.com.fiap.territory.dominio.Sensor;
import br.com.fiap.territory.dominio.SensorCargaTransformador;
import br.com.fiap.territory.dominio.SensorConsumoEnergia;
import br.com.fiap.territory.dominio.SensorTemperatura;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SensoresTerritorio {

    private static final String REGIAO = "Zona Leste";

    @Bean
    public Sensor sensorTemperatura() {
        return new SensorTemperatura("TEMP-ZL-01", "Termometro da Praça Central", REGIAO);
    }

    @Bean
    public Sensor sensorCargaTransformador() {
        return new SensorCargaTransformador("CARGA-ZL-01", "Transformador T-07 (Rua das Flores)", REGIAO);
    }

    @Bean
    public Sensor sensorConsumoEnergia() {
        return new SensorConsumoEnergia("ENERGIA-ZL-01", "Medidor do Alimentador A3", REGIAO);
    }

    @Bean
    public ConfiguracaoSistema configuracaoSistema() {
        return ConfiguracaoSistema.getInstancia();
    }
}
