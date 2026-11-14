package br.com.fiap.territory.servico;

import br.com.fiap.territory.dominio.Alerta;
import br.com.fiap.territory.dominio.NivelAlerta;
import br.com.fiap.territory.dominio.TipoSensor;

public record ResultadoPrevisao(
        String sensorId,
        TipoSensor tipo,
        String estrategia,
        double valorPrevisto,
        NivelAlerta nivel,
        String interpretacao,
        Alerta alerta) {
}
