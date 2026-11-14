package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MedicaoTest {
    @Test
    void criaMedicaoQuandoValorEstaNaFaixa() throws DadosInvalidosException {
        Medicao medicao = new Medicao("TEMP-01", TipoSensor.TEMPERATURA, 36.5);
        assertEquals(36.5, medicao.getValor());
        assertEquals("C", medicao.getUnidade());
    }

    @Test
    void lancaDadosInvalidosQuandoConsumoNegativo() {
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> new Medicao("ENERGIA-01", TipoSensor.CONSUMO_ENERGIA, -5.0));
        assertTrue(erro.getMessage().contains("fora da faixa válida"));
    }

    @Test
    void lancaDadosInvalidosQuandoTemperaturaAcimaDaFaixaFisica() {
        assertThrows(DadosInvalidosException.class,
                () -> new Medicao("TEMP-01", TipoSensor.TEMPERATURA, 75.0));
    }

    @Test
    void lancaDadosInvalidosQuandoValorNaoENumero() {
        assertThrows(DadosInvalidosException.class,
                () -> new Medicao("TEMP-01", TipoSensor.TEMPERATURA, Double.NaN));
    }
}