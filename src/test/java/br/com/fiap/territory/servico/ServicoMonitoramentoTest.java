package br.com.fiap.territory.servico;

import br.com.fiap.territory.dominio.Alerta;
import br.com.fiap.territory.dominio.EstrategiaPrevisao;
import br.com.fiap.territory.dominio.Medicao;
import br.com.fiap.territory.dominio.NivelAlerta;
import br.com.fiap.territory.dominio.PrevisaoMediaMovel;
import br.com.fiap.territory.dominio.Sensor;
import br.com.fiap.territory.dominio.TipoSensor;
import br.com.fiap.territory.excecao.DadosInvalidosException;
import br.com.fiap.territory.excecao.FonteIndisponivelException;
import br.com.fiap.territory.persistencia.AlertaRepository;
import br.com.fiap.territory.persistencia.MedicaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ServicoMonitoramentoTest {

    @Mock
    private MedicaoRepository medicaoRepository;
    @Mock
    private AlertaRepository alertaRepository;
    @Mock
    private Sensor sensorTemperatura;

    private ServicoMonitoramento servico;
    private EstrategiaPrevisao mediaMovel;

    @BeforeEach
    void setUp() {
        ConfiguracaoSistema configuracao = ConfiguracaoSistema.getInstancia();
        configuracao.resetarPadrao();
        when(sensorTemperatura.getId()).thenReturn("TEMP-01");
        when(sensorTemperatura.getNome()).thenReturn("Termometro de teste");
        when(sensorTemperatura.getRegiao()).thenReturn("Zona Teste");
        when(sensorTemperatura.getTipo()).thenReturn(TipoSensor.TEMPERATURA);
        servico = new ServicoMonitoramento(medicaoRepository, alertaRepository,
                List.of(sensorTemperatura), configuracao);
        mediaMovel = new PrevisaoMediaMovel(5, 5);
    }

    private List<Medicao> historicoDoBanco(double... valoresCronologicos) throws DadosInvalidosException {
        List<Medicao> lista = new ArrayList<>();
        for (int i = valoresCronologicos.length - 1; i >= 0; i--) {
            lista.add(new Medicao("TEMP-01", TipoSensor.TEMPERATURA, valoresCronologicos[i]));
        }
        return lista;
    }

    @Test
    void emiteAlertaQuandoPrevisaoUltrapassaLimiar() throws Exception {
        when(medicaoRepository.findTop20BySensorIdOrderByIdDesc("TEMP-01"))
                .thenReturn(historicoDoBanco(38, 39, 40, 41, 42));

        ResultadoPrevisao resultado = servico.preverEAvaliar(sensorTemperatura, mediaMovel);

        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository).save(captor.capture());
        Alerta salvo = captor.getValue();
        assertEquals(NivelAlerta.ALERTA, resultado.nivel());
        assertEquals(NivelAlerta.ALERTA, salvo.getNivel());
        assertEquals(40.0, salvo.getValorPrevisto(), 0.0001);
        assertEquals(38.0, salvo.getLimiteEstourado(), 0.0001);
        assertTrue(salvo.getMensagem().contains("TEMP-01"));
        assertTrue(salvo.getMensagem().contains("38.00"));
    }

    @Test
    void naoEmiteAlertaQuandoPrevisaoEstaNormal() throws Exception {
        when(medicaoRepository.findTop20BySensorIdOrderByIdDesc("TEMP-01"))
                .thenReturn(historicoDoBanco(25, 26, 27, 26, 25));

        ResultadoPrevisao resultado = servico.preverEAvaliar(sensorTemperatura, mediaMovel);
        assertEquals(NivelAlerta.NORMAL, resultado.nivel());
        assertNull(resultado.alerta());
        verify(alertaRepository, never()).save(any(Alerta.class));
    }

    @Test
    void classificaComoCriticoQuandoPrevisaoPassaDoLimiteCritico() throws Exception {
        when(medicaoRepository.findTop20BySensorIdOrderByIdDesc("TEMP-01"))
                .thenReturn(historicoDoBanco(42, 43, 43, 43, 44));

        ResultadoPrevisao resultado = servico.preverEAvaliar(sensorTemperatura, mediaMovel);
        assertEquals(NivelAlerta.CRITICO, resultado.nivel());
        verify(alertaRepository).save(any(Alerta.class));
    }

    @Test
    void naoPrevQuandoHistoricoEInsuficiente() throws Exception {
        when(medicaoRepository.findTop20BySensorIdOrderByIdDesc("TEMP-01"))
                .thenReturn(historicoDoBanco(40, 41, 42));
        assertThrows(DadosInvalidosException.class,
                () -> servico.preverEAvaliar(sensorTemperatura, mediaMovel));
        verify(alertaRepository, never()).save(any(Alerta.class));
    }

    @Test
    void gravaMedicaoValidaLidaDoSensor() throws Exception {
        when(sensorTemperatura.lerValor()).thenReturn(36.5);
        Medicao medicao = servico.lerESalvar(sensorTemperatura);
        ArgumentCaptor<Medicao> captor = ArgumentCaptor.forClass(Medicao.class);
        verify(medicaoRepository).save(captor.capture());
        assertEquals(36.5, captor.getValue().getValor(), 0.0001);
        assertEquals("TEMP-01", medicao.getSensorId());
    }

    @Test
    void naoGravaQuandoSensorFalha() throws Exception {
        when(sensorTemperatura.lerValor()).thenThrow(new FonteIndisponivelException("sensor fora do ar"));
        assertThrows(FonteIndisponivelException.class, () -> servico.lerESalvar(sensorTemperatura));
        verify(medicaoRepository, never()).save(any(Medicao.class));
    }

    @Test
    void rejeitaLeituraManualForaDaFaixaSemGravar() {
        assertThrows(DadosInvalidosException.class,
                () -> servico.registrarLeituraManual("TEMP-01", -50.0));
        verify(medicaoRepository, never()).save(any(Medicao.class));
    }

    @Test
    void rejeitaLeituraManualDeSensorInexistente() {
        assertThrows(DadosInvalidosException.class,
                () -> servico.registrarLeituraManual("NAO-EXISTE", 30.0));
    }
}
