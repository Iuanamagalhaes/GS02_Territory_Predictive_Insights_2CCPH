package br.com.fiap.territory.servico;

import br.com.fiap.territory.dominio.Alerta;
import br.com.fiap.territory.dominio.EstrategiaPrevisao;
import br.com.fiap.territory.dominio.Limiar;
import br.com.fiap.territory.dominio.Medicao;
import br.com.fiap.territory.dominio.NivelAlerta;
import br.com.fiap.territory.dominio.Sensor;
import br.com.fiap.territory.dominio.TipoSensor;
import br.com.fiap.territory.excecao.DadosInvalidosException;
import br.com.fiap.territory.excecao.FonteIndisponivelException;
import br.com.fiap.territory.persistencia.AlertaRepository;
import br.com.fiap.territory.persistencia.MedicaoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
public class ServicoMonitoramento {

    private final MedicaoRepository medicaoRepository;
    private final AlertaRepository alertaRepository;
    private final List<Sensor> sensores;
    private final ConfiguracaoSistema configuracao;

    public ServicoMonitoramento(MedicaoRepository medicaoRepository, AlertaRepository alertaRepository,
                                List<Sensor> sensores, ConfiguracaoSistema configuracao) {
        this.medicaoRepository = medicaoRepository;
        this.alertaRepository = alertaRepository;
        this.sensores = sensores;
        this.configuracao = configuracao;
    }

    public List<Sensor> getSensores() {
        return sensores;
    }

    public List<EstrategiaPrevisao> criarEstrategias() {
        return FabricaPrevisao.criarTodas(configuracao);
    }

    public Sensor buscarSensor(String sensorId) throws DadosInvalidosException {
        return sensores.stream()
                .filter(s -> s.getId().equalsIgnoreCase(sensorId))
                .findFirst()
                .orElseThrow(() -> new DadosInvalidosException("Sensor não encontrado: " + sensorId));
    }

    public Medicao lerESalvar(Sensor sensor) throws FonteIndisponivelException, DadosInvalidosException {
        double valor = sensor.lerValor();
        Medicao medicao = new Medicao(sensor.getId(), sensor.getTipo(), valor);
        medicaoRepository.save(medicao);
        return medicao;
    }

    public Medicao registrarLeituraManual(String sensorId, double valor) throws DadosInvalidosException {
        Sensor sensor = buscarSensor(sensorId);
        Medicao medicao = new Medicao(sensor.getId(), sensor.getTipo(), valor);
        medicaoRepository.save(medicao);
        return medicao;
    }

    public ResultadoPrevisao preverEAvaliar(Sensor sensor, EstrategiaPrevisao estrategia)
            throws DadosInvalidosException {
        List<Medicao> recentes = new ArrayList<>(
                medicaoRepository.findTop20BySensorIdOrderByIdDesc(sensor.getId()));
        Collections.reverse(recentes);
        List<Double> valores = recentes.stream().map(Medicao::getValor).toList();

        double previsto = Math.round(estrategia.prever(valores) * 100.0) / 100.0;

        TipoSensor tipo = sensor.getTipo();
        Limiar limiar = configuracao.getDadosTerritorio().getLimiar(tipo);
        NivelAlerta nivel = limiar.classificar(previsto);
        String unidade = tipo.getUnidade();

        if (nivel == NivelAlerta.NORMAL) {
            String interpretacao = String.format(Locale.US,
                    "Previsão de %.2f %s abaixo do limite de ATENCAO (%.2f %s): sem risco previsto.",
                    previsto, unidade, limiar.getAtencao(), unidade);
            return new ResultadoPrevisao(sensor.getId(), tipo, estrategia.getNome(), previsto,
                    nivel, interpretacao, null);
        }

        double limite = limiar.limiteDo(nivel);
        String mensagem = String.format(Locale.US,
                "[%s] Sensor %s (%s, %s): previsão de %.2f %s (%s) atinge o limite %s de %.2f %s. "
                        + "Possível %s. Sugestão: %s. A decisão final e da equipe responsável.",
                nivel, sensor.getId(), sensor.getNome(), sensor.getRegiao(), previsto, unidade,
                estrategia.getNome(), nivel, limite, unidade, tipo.getRisco(), nivel.getOrientacao());

        Alerta alerta = new Alerta(sensor.getId(), tipo, nivel, previsto, limite,
                estrategia.getNome(), mensagem);
        alertaRepository.save(alerta);

        return new ResultadoPrevisao(sensor.getId(), tipo, estrategia.getNome(), previsto,
                nivel, mensagem, alerta);
    }

    public List<Medicao> buscarHistoricoPorSensor(String sensorId) {
        return medicaoRepository.findBySensorIdOrderByIdAsc(sensorId);
    }

    public List<Medicao> buscarUltimasMedicoes(String sensorId) {
        return medicaoRepository.findTop20BySensorIdOrderByIdDesc(sensorId);
    }

    public long contarMedicoes(String sensorId) {
        return medicaoRepository.countBySensorId(sensorId);
    }

    public List<Alerta> listarAlertas() {
        return alertaRepository.findAllByOrderByIdDesc();
    }

    public long contarAlertas() {
        return alertaRepository.count();
    }
}
