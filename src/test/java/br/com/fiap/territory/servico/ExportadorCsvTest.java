package br.com.fiap.territory.servico;

import br.com.fiap.territory.dominio.Alerta;
import br.com.fiap.territory.dominio.Medicao;
import br.com.fiap.territory.dominio.NivelAlerta;
import br.com.fiap.territory.dominio.TipoSensor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExportadorCsvTest {

    @TempDir
    Path pasta;

    @Test
    void exportaMedicoesComCabecalhoEValoresEmFormatoBrasileiro() throws Exception {
        ExportadorCsv exportador = new ExportadorCsv(pasta);
        List<Medicao> medicoes = List.of(
                new Medicao("TEMP-01", TipoSensor.TEMPERATURA, 36.5),
                new Medicao("TEMP-01", TipoSensor.TEMPERATURA, 38.25));
        Path arquivo = exportador.exportarMedicoes(medicoes, "TEMP-01");
        List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
        assertEquals(3, linhas.size());
        assertTrue(linhas.get(0).endsWith("id;sensor_id;tipo_sensor;valor;unidade;data_hora"));
        assertTrue(linhas.get(1).contains(";TEMP-01;TEMPERATURA;36,50;C;"));
        assertTrue(linhas.get(2).contains("38,25"));
    }

    @Test
    void protegeCamposComPontoEVirgulaEAspas() {
        assertEquals("\"a;b\"", ExportadorCsv.escapar("a;b"));
        assertEquals("\"diz \"\"oi\"\"\"", ExportadorCsv.escapar("diz \"oi\""));
        assertEquals("simples", ExportadorCsv.escapar("simples"));
        assertEquals("", ExportadorCsv.escapar(null));
    }

    @Test
    void criaAPastaEExportaAlertas() throws Exception {

        Path destino = pasta.resolve("exports");
        ExportadorCsv exportador = new ExportadorCsv(destino);
        Alerta alerta = new Alerta("TEMP-01", TipoSensor.TEMPERATURA, NivelAlerta.ALERTA,
                40.0, 38.0, "Média móvel", "Mensagem; com ponto e vírgula");

        Path arquivo = exportador.exportarAlertas(List.of(alerta));
        List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
        assertTrue(Files.exists(arquivo));
        assertEquals(2, linhas.size());
        assertTrue(linhas.get(1).contains("ALERTA;40,00;38,00;Média móvel"));
        assertTrue(linhas.get(1).endsWith("\"Mensagem; com ponto e vírgula\""));
    }
}