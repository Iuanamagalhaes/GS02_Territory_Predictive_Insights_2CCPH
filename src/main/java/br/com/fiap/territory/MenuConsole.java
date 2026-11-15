package br.com.fiap.territory;

import br.com.fiap.territory.dominio.Alerta;
import br.com.fiap.territory.dominio.DadosTerritorio;
import br.com.fiap.territory.dominio.EstrategiaPrevisao;
import br.com.fiap.territory.dominio.Limiar;
import br.com.fiap.territory.dominio.Medicao;
import br.com.fiap.territory.dominio.Sensor;
import br.com.fiap.territory.dominio.TipoSensor;
import br.com.fiap.territory.excecao.DadosInvalidosException;
import br.com.fiap.territory.excecao.FonteIndisponivelException;
import br.com.fiap.territory.servico.ConfiguracaoSistema;
import br.com.fiap.territory.servico.ExportadorCsv;
import br.com.fiap.territory.servico.ResultadoPrevisao;
import br.com.fiap.territory.servico.ServicoMonitoramento;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

@Component
public class MenuConsole implements CommandLineRunner {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final ServicoMonitoramento servico;
    private final ExportadorCsv exportador;
    private final ConfiguracaoSistema configuracao;
    private Scanner scanner;

    public MenuConsole(ServicoMonitoramento servico, ExportadorCsv exportador,
                       ConfiguracaoSistema configuracao) {
        this.servico = servico;
        this.exportador = exportador;
        this.configuracao = configuracao;
    }

    @Override
    public void run(String... args) {
        scanner = new Scanner(System.in);
        imprimirCabecalho();
        try {
            boolean executando = true;
            while (executando) {
                exibirMenu();
                String opcao = lerLinha("Escolha uma opção: ").trim();
                try {
                    switch (opcao) {
                        case "1" -> verificarSensores();
                        case "2" -> rodarPrevisoes();
                        case "3" -> menuHistorico();
                        case "4" -> menuDadosTerritorio();
                        case "5" -> exibirStatusCompleto();
                        case "6" -> registrarLeituraManual();
                        case "0" -> executando = false;
                        default -> System.out.println("Opção inválida: '" + opcao + "'. Escolha um número do menu.");
                    }
                } catch (EntradaEncerradaException e) {
                    throw e;
                } catch (RuntimeException e) {
                    System.out.println("[ERRO INESPERADO] " + e.getMessage());
                }
            }
            System.out.println("Encerrando. Medições e alertas ficaram salvos no banco.");
        } catch (EntradaEncerradaException e) {
            System.out.println("\nEntrada encerrada. Finalizando.");
        }
    }

    private void imprimirCabecalho() {
        DadosTerritorio t = configuracao.getDadosTerritorio();
        System.out.println();
        System.out.println("==============================================================");
        System.out.println("  TERRITORY PREDICTIVE INSIGHTS - Calor e Energia");
        System.out.println("  Território: " + t.getNome() + " (" + t.getRegiao() + ")");
        System.out.println("==============================================================");
    }

    private void exibirMenu() {
        System.out.println();
        System.out.println("1) Verificar sensores (ler e gravar medições)");
        System.out.println("2) Rodar previsões e alertas");
        System.out.println("3) Ver histórico (medições / alertas / exportar CSV)");
        System.out.println("4) Gerenciar dados do território");
        System.out.println("5) Status completo");
        System.out.println("6) Registrar leitura manual");
        System.out.println("0) Sair");
    }

    private void verificarSensores() {
        Integer rodadas = lerInteiro("Quantas rodadas de leitura? (1 a 100) [Enter = 1]: ", 1);
        if (rodadas == null) {
            return;
        }
        if (rodadas < 1 || rodadas > 100) {
            System.out.println("[DADO INVÁLIDO] Informe um número de rodadas entre 1 e 100.");
            return;
        }
        int gravadas = 0;
        int falhas = 0;
        int descartadas = 0;
        for (int rodada = 1; rodada <= rodadas; rodada++) {
            for (Sensor sensor : servico.getSensores()) {
                try {
                    Medicao medicao = servico.lerESalvar(sensor);
                    gravadas++;
                    if (rodadas == 1) {
                        System.out.printf(Locale.US, "  [OK] %-14s %-38s %9.2f %s%n", sensor.getId(),
                                sensor.getNome(), medicao.getValor(), medicao.getUnidade());
                    }
                } catch (FonteIndisponivelException e) {
                    falhas++;
                    if (rodada == 1) {
                        System.out.println("  [FALHA] " + e.getMessage());
                    }
                } catch (DadosInvalidosException e) {
                    descartadas++;
                    if (rodada == 1) {
                        System.out.println("  [LEITURA DESCARTADA] " + e.getMessage());
                    }
                }
            }
        }
        System.out.println("Resumo: " + gravadas + " medição(ões) gravada(s), " + falhas
                + " falha(s) de sensor, " + descartadas + " descartada(s) por valor inválido.");
    }

    private void rodarPrevisoes() {
        List<EstrategiaPrevisao> estrategias = servico.criarEstrategias();
        int alertasEmitidos = 0;
        for (Sensor sensor : servico.getSensores()) {
            System.out.println();
            System.out.println("Sensor " + sensor.getId() + " - " + sensor.getNome());
            for (EstrategiaPrevisao estrategia : estrategias) {
                try {
                    ResultadoPrevisao r = servico.preverEAvaliar(sensor, estrategia);
                    System.out.printf(Locale.US, "  - %s: previsto %.2f %s -> %s%n", r.estrategia(),
                            r.valorPrevisto(), r.tipo().getUnidade(), r.nivel());
                    System.out.println("      " + r.interpretacao());
                    if (r.alerta() != null) {
                        alertasEmitidos++;
                    }
                } catch (DadosInvalidosException e) {
                    System.out.println("  - " + estrategia.getNome() + ": [SEM PREVISÃO] " + e.getMessage());
                }
            }
        }
        System.out.println();
        System.out.println("Alertas emitidos e gravados nesta execução: " + alertasEmitidos);
    }

    private void menuHistorico() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println();
            System.out.println("--- Histórico ---");
            System.out.println("1) Medições por sensor");
            System.out.println("2) Alertas já emitidos");
            System.out.println("0) Voltar");
            String opcao = lerLinha("Escolha uma opção: ").trim();
            switch (opcao) {
                case "1" -> historicoMedicoes();
                case "2" -> historicoAlertas();
                case "0" -> voltar = true;
                default -> System.out.println("Opção inválida: '" + opcao + "'.");
            }
        }
    }

    private void historicoMedicoes() {
        Sensor sensor = escolherSensor();
        if (sensor == null) {
            return;
        }
        List<Medicao> ultimas = servico.buscarUltimasMedicoes(sensor.getId());
        long total = servico.contarMedicoes(sensor.getId());
        if (ultimas.isEmpty()) {
            System.out.println("Nenhuma medição gravada para " + sensor.getId() + ". Use a opção 1 do menu.");
            return;
        }
        System.out.println("Medições de " + sensor.getId() + " (mais recentes primeiro):");
        for (Medicao m : ultimas) {
            System.out.printf(Locale.US, "  %s  %9.2f %s%n", m.getDataHora().format(FORMATO_DATA),
                    m.getValor(), m.getUnidade());
        }
        System.out.println("Exibindo " + ultimas.size() + " de " + total + " medição(ões).");

        if (confirmar("Exportar o histórico COMPLETO deste sensor para CSV? (s/n): ")) {
            try {
                Path arquivo = exportador.exportarMedicoes(
                        servico.buscarHistoricoPorSensor(sensor.getId()), sensor.getId());
                System.out.println("Arquivo gerado: " + arquivo.toAbsolutePath());
            } catch (IOException e) {
                System.out.println("[ERRO] Não foi possível gravar o CSV: " + e.getMessage());
            }
        }
    }

    private void historicoAlertas() {
        List<Alerta> alertas = servico.listarAlertas();
        if (alertas.isEmpty()) {
            System.out.println("Nenhum alerta emitido ainda. Rode as previsões (opção 2).");
            return;
        }
        int limite = Math.min(alertas.size(), 30);
        System.out.println("Alertas emitidos (mais recentes primeiro):");
        for (int i = 0; i < limite; i++) {
            Alerta a = alertas.get(i);
            System.out.printf(Locale.US, "  %s [%s] %s: previsto %.2f %s > limite %.2f (%s)%n",
                    a.getDataHora().format(FORMATO_DATA), a.getNivel(), a.getSensorId(),
                    a.getValorPrevisto(), a.getTipoSensor().getUnidade(), a.getLimiteEstourado(),
                    a.getEstrategia());
        }
        System.out.println("Exibindo " + limite + " de " + alertas.size() + " alerta(s).");

        if (confirmar("Exportar TODOS os alertas para CSV? (s/n): ")) {
            try {
                Path arquivo = exportador.exportarAlertas(alertas);
                System.out.println("Arquivo gerado: " + arquivo.toAbsolutePath());
            } catch (IOException e) {
                System.out.println("[ERRO] Não foi possível gravar o CSV: " + e.getMessage());
            }
        }
    }

    private void menuDadosTerritorio() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println();
            System.out.println("--- Dados do território ---");
            System.out.println("1) Ver dados e limiares");
            System.out.println("2) Alterar população atendida");
            System.out.println("3) Alterar limiares de um sensor");
            System.out.println("4) Simular falha / recuperar sensor");
            System.out.println("0) Voltar");
            String opcao = lerLinha("Escolha uma opção: ").trim();
            switch (opcao) {
                case "1" -> exibirDadosTerritorio();
                case "2" -> alterarPopulacao();
                case "3" -> alterarLimiares();
                case "4" -> alternarFalhaSensor();
                case "0" -> voltar = true;
                default -> System.out.println("Opção inválida: '" + opcao + "'.");
            }
        }
    }

    private void exibirDadosTerritorio() {
        DadosTerritorio t = configuracao.getDadosTerritorio();
        System.out.println("Território: " + t.getNome() + " | Região: " + t.getRegiao()
                + " | População atendida: " + t.getPopulacao());
        for (TipoSensor tipo : TipoSensor.values()) {
            Limiar l = t.getLimiar(tipo);
            System.out.printf(Locale.US, "  %-24s ATENCAO >= %.2f | ALERTA >= %.2f | CRITICO >= %.2f %s%n",
                    tipo.getDescricao(), l.getAtencao(), l.getAlerta(), l.getCritico(), tipo.getUnidade());
        }
    }

    private void alterarPopulacao() {
        Integer nova = lerInteiro("Nova população atendida: ", null);
        if (nova == null) {
            return;
        }
        try {
            configuracao.getDadosTerritorio().setPopulacao(nova);
            System.out.println("População atualizada para " + nova + ".");
        } catch (DadosInvalidosException e) {
            System.out.println("[DADO INVÁLIDO] " + e.getMessage());
        }
    }

    private void alterarLimiares() {
        TipoSensor[] tipos = TipoSensor.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.println("  " + (i + 1) + ") " + tipos[i].getDescricao() + " (" + tipos[i].getUnidade() + ")");
        }
        Integer indice = lerInteiro("Tipo de sensor (número): ", null);
        if (indice == null) {
            return;
        }
        if (indice < 1 || indice > tipos.length) {
            System.out.println("[DADO INVÁLIDO] Escolha um número entre 1 e " + tipos.length + ".");
            return;
        }
        TipoSensor tipo = tipos[indice - 1];
        Double atencao = lerDecimal("Novo limiar de ATENÇÃO: ");
        Double alerta = lerDecimal("Novo limiar de ALERTA: ");
        Double critico = lerDecimal("Novo limiar de CRÍTICO: ");
        if (atencao == null || alerta == null || critico == null) {
            return;
        }
        try {
            configuracao.getDadosTerritorio().definirLimiar(tipo, atencao, alerta, critico);
            System.out.println("Limiares de " + tipo.getDescricao() + " atualizados.");
        } catch (DadosInvalidosException e) {
            System.out.println("[DADO INVÁLIDO] " + e.getMessage());
        }
    }

    private void alternarFalhaSensor() {
        Sensor sensor = escolherSensor();
        if (sensor == null) {
            return;
        }
        sensor.simularFalha(sensor.estaFuncionando());
        System.out.println("Sensor " + sensor.getId() + " agora está: " + sensor.verificarStatus());
    }

    private void exibirStatusCompleto() {
        System.out.println();
        System.out.println("=========== STATUS COMPLETO ===========");
        exibirDadosTerritorio();
        System.out.println();
        System.out.println("Sensores:");
        for (Sensor sensor : servico.getSensores()) {
            System.out.printf("  %-14s %-38s %-12s %d medição(ões)%n", sensor.getId(), sensor.getNome(),
                    sensor.verificarStatus(), servico.contarMedicoes(sensor.getId()));
        }
        System.out.println();
        System.out.println("Alertas já emitidos: " + servico.contarAlertas());
        System.out.println("Mínimo de leituras para prever: " + configuracao.getMinimoLeituras());
        System.out.println("Previsões disponíveis:");
        for (EstrategiaPrevisao e : servico.criarEstrategias()) {
            System.out.println("  - " + e.getNome() + ": " + e.descrever());
        }
    }

    private void registrarLeituraManual() {
        Sensor sensor = escolherSensor();
        if (sensor == null) {
            return;
        }
        Double valor = lerDecimal("Valor lido (" + sensor.getTipo().getUnidade() + "): ");
        if (valor == null) {
            return;
        }
        try {
            Medicao m = servico.registrarLeituraManual(sensor.getId(), valor);
            System.out.printf(Locale.US, "Leitura gravada: %.2f %s em %s%n", m.getValor(), m.getUnidade(),
                    m.getDataHora().format(FORMATO_DATA));
        } catch (DadosInvalidosException e) {
            System.out.println("[DADO INVÁLIDO] " + e.getMessage());
        }
    }

    private Sensor escolherSensor() {
        List<Sensor> sensores = servico.getSensores();
        for (int i = 0; i < sensores.size(); i++) {
            Sensor s = sensores.get(i);
            System.out.println("  " + (i + 1) + ") " + s.getId() + " - " + s.getNome()
                    + " (" + s.getTipo().getDescricao() + ")");
        }
        Integer indice = lerInteiro("Sensor (número): ", null);
        if (indice == null) {
            return null;
        }
        if (indice < 1 || indice > sensores.size()) {
            System.out.println("[DADO INVÁLIDO] Escolha um número entre 1 e " + sensores.size() + ".");
            return null;
        }
        return sensores.get(indice - 1);
    }

    private String lerLinha(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new EntradaEncerradaException();
        }
        return scanner.nextLine();
    }

    private boolean confirmar(String prompt) {
        return lerLinha(prompt).trim().equalsIgnoreCase("s");
    }

    private Integer lerInteiro(String prompt, Integer padrao) {
        String texto = lerLinha(prompt).trim();
        if (texto.isEmpty()) {
            if (padrao == null) {
                System.out.println("[DADO INVÁLIDO] Nenhum valor informado.");
            }
            return padrao;
        }
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            System.out.println("[DADO INVÁLIDO] '" + texto + "' não é um número inteiro.");
            return null;
        }
    }

    private Double lerDecimal(String prompt) {
        String texto = lerLinha(prompt).trim().replace(',', '.');
        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            System.out.println("[DADO INVÁLIDO] '" + texto + "' não é um número válido.");
            return null;
        }
    }

    private static class EntradaEncerradaException extends RuntimeException {
        EntradaEncerradaException() {
            super("entrada encerrada");
        }
    }
}