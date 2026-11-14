package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrevisaoMediaMovelTest {

    @Test
    void calculaMediaDasUltimasLeituras() throws DadosInvalidosException {
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel(5, 5);
        List<Double> historico = List.of(30.0, 32.0, 34.0, 36.0, 38.0);
        double resultado = previsao.prever(historico);
        assertEquals(34.0, resultado, 0.0001);
    }

    @Test
    void usaSomenteAsUltimasNLeituras() throws DadosInvalidosException {
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel(3, 5);
        List<Double> historico = List.of(10.0, 10.0, 30.0, 33.0, 36.0);
        double resultado = previsao.prever(historico);
        assertEquals(33.0, resultado, 0.0001);
    }

    @Test
    void lancaDadosInvalidosQuandoHistoricoTemMenosDoMinimo() {
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel(5, 5);
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> previsao.prever(List.of(30.0, 31.0, 32.0)));
        assertEquals("Histórico insuficiente para a previsão 'Média móvel': 3 leitura(s) no banco, mínimo de 5.",
                erro.getMessage());
    }
}