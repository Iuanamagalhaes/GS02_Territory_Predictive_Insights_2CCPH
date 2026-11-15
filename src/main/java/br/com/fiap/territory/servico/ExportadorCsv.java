package br.com.fiap.territory.servico;

import br.com.fiap.territory.dominio.Alerta;
import br.com.fiap.territory.dominio.Medicao;
import org.springframework.stereotype.Component;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
public class ExportadorCsv {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter SUFIXO = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final Path pastaDestino;

    public ExportadorCsv() {
        this(Path.of("exports"));
    }

    public ExportadorCsv(Path pastaDestino) {
        this.pastaDestino = pastaDestino;
    }

    public Path exportarMedicoes(List<Medicao> medicoes, String sensorId) throws IOException {
        Path arquivo = prepararArquivo("medicoes_" + nomeSeguro(sensorId));
        try (BufferedWriter escritor = Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8)) {
            escritor.write('\uFEFF');
            escritor.write("id;sensor_id;tipo_sensor;valor;unidade;data_hora");
            escritor.newLine();
            for (Medicao m : medicoes) {
                escritor.write(String.join(";",
                        String.valueOf(m.getId()),
                        escapar(m.getSensorId()),
                        m.getTipoSensor().name(),
                        String.format(PT_BR, "%.2f", m.getValor()),
                        escapar(m.getUnidade()),
                        m.getDataHora().format(DATA)));
                escritor.newLine();
            }
        }
        return arquivo;
    }

    public Path exportarAlertas(List<Alerta> alertas) throws IOException {
        Path arquivo = prepararArquivo("alertas");
        try (BufferedWriter escritor = Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8)) {
            escritor.write('\uFEFF');
            escritor.write("id;sensor_id;tipo_sensor;nivel;valor_previsto;limite_estourado;estrategia;data_hora;mensagem");
            escritor.newLine();
            for (Alerta a : alertas) {
                escritor.write(String.join(";",
                        String.valueOf(a.getId()),
                        escapar(a.getSensorId()),
                        a.getTipoSensor().name(),
                        a.getNivel().name(),
                        String.format(PT_BR, "%.2f", a.getValorPrevisto()),
                        String.format(PT_BR, "%.2f", a.getLimiteEstourado()),
                        escapar(a.getEstrategia()),
                        a.getDataHora().format(DATA),
                        escapar(a.getMensagem())));
                escritor.newLine();
            }
        }
        return arquivo;
    }

    private Path prepararArquivo(String prefixo) throws IOException {
        Files.createDirectories(pastaDestino);
        return pastaDestino.resolve(prefixo + "_" + LocalDateTime.now().format(SUFIXO) + ".csv");
    }

    private static String nomeSeguro(String texto) {
        return texto.replaceAll("[^A-Za-z0-9_-]", "_");
    }

    static String escapar(String campo) {
        if (campo == null) {
            return "";
        }
        if (campo.contains(";") || campo.contains("\"") || campo.contains("\n") || campo.contains("\r")) {
            return "\"" + campo.replace("\"", "\"\"") + "\"";
        }
        return campo;
    }
}