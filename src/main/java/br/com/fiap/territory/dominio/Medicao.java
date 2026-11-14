package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "MEDICAO", indexes = @Index(name = "IDX_MEDICAO_SENSOR", columnList = "SENSOR_ID"))
public class Medicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SENSOR_ID", nullable = false, length = 50)
    private String sensorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_SENSOR", nullable = false, length = 30)
    private TipoSensor tipoSensor;

    @Column(name = "VALOR", nullable = false)
    private double valor;

    @Column(name = "DATA_HORA", nullable = false)
    private LocalDateTime dataHora;

    protected Medicao() {
    }

    public Medicao(String sensorId, TipoSensor tipoSensor, double valor) throws DadosInvalidosException {
        this(sensorId, tipoSensor, valor, LocalDateTime.now());
    }

    public Medicao(String sensorId, TipoSensor tipoSensor, double valor, LocalDateTime dataHora)
            throws DadosInvalidosException {
        setSensorId(sensorId);
        setTipoSensor(tipoSensor);
        setValor(valor);
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public String getSensorId() {
        return sensorId;
    }

    private void setSensorId(String sensorId) throws DadosInvalidosException {
        if (sensorId == null || sensorId.isBlank()) {
            throw new DadosInvalidosException("A medição precisa informar o id do sensor.");
        }
        this.sensorId = sensorId.trim();
    }

    public TipoSensor getTipoSensor() {
        return tipoSensor;
    }

    private void setTipoSensor(TipoSensor tipoSensor) throws DadosInvalidosException {
        if (tipoSensor == null) {
            throw new DadosInvalidosException("A medição precisa informar o tipo do sensor.");
        }
        this.tipoSensor = tipoSensor;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) throws DadosInvalidosException {
        tipoSensor.validar(valor);
        this.valor = valor;
    }

    public String getUnidade() {
        return tipoSensor.getUnidade();
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}
