package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PrevisaoComparacaoTest {
    @Test
    void projetaATendenciaDeAltaDoPeriodoRecente() throws DadosInvalidosException {
        PrevisaoComparacao previsao = new PrevisaoComparacao(3, 5);
        List<Double> historico = List.of(30.0, 30.0, 30.0, 36.0, 36.0, 36.0);
        double resultado = previsao.prever(historico);
        assertEquals(42.0, resultado, 0.0001);
    }

    @Test
    void mantemOValorQuandoNaoHaTendencia() throws DadosInvalidosException {
        PrevisaoComparacao previsao = new PrevisaoComparacao(3, 5);
        List<Double> historico = List.of(33.0, 33.0, 33.0, 33.0, 33.0, 33.0);
        double resultado = previsao.prever(historico);
        assertEquals(33.0, resultado, 0.0001);
    }

    @Test
    void estrategiasDiferentesDaoPrevisoesDiferentesParaOMesmoHistorico() throws DadosInvalidosException {
        List<Double> historico = List.of(30.0, 30.0, 30.0, 36.0, 36.0, 36.0);
        EstrategiaPrevisao mediaMovel = new PrevisaoMediaMovel(6, 5);
        EstrategiaPrevisao comparacao = new PrevisaoComparacao(3, 5);
        double porMedia = mediaMovel.prever(historico);
        double porComparacao = comparacao.prever(historico);
        assertEquals(33.0, porMedia, 0.0001);
        assertNotEquals(porMedia, porComparacao);
    }
}