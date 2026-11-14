package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DadosTerritorioTest {

    @Test
    void rejeitaPopulacaoNegativa() throws DadosInvalidosException {
        DadosTerritorio territorio = new DadosTerritorio("Vila Teste", "Zona Teste", 1000);
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> territorio.setPopulacao(-10));
        assertEquals("População não pode ser negativa: -10", erro.getMessage());
        assertEquals(1000, territorio.getPopulacao());
    }

    @Test
    void rejeitaLimiarForaDeOrdem() throws DadosInvalidosException {
        DadosTerritorio territorio = new DadosTerritorio("Vila Teste", "Zona Teste", 1000);
        assertThrows(DadosInvalidosException.class,
                () -> territorio.definirLimiar(TipoSensor.TEMPERATURA, 40.0, 38.0, 41.0));
    }

    @Test
    void classificaValoresNosLimites() throws DadosInvalidosException {
        DadosTerritorio territorio = new DadosTerritorio("Vila Teste", "Zona Teste", 1000);
        territorio.definirLimiar(TipoSensor.TEMPERATURA, 35.0, 38.0, 41.0);
        Limiar limiar = territorio.getLimiar(TipoSensor.TEMPERATURA);
        assertEquals(NivelAlerta.NORMAL, limiar.classificar(34.99));
        assertEquals(NivelAlerta.ATENCAO, limiar.classificar(35.0));
        assertEquals(NivelAlerta.ALERTA, limiar.classificar(38.0));
        assertEquals(NivelAlerta.CRITICO, limiar.classificar(41.0));
    }
}
