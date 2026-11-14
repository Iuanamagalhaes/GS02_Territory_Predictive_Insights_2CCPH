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
@Table(name = "ALERTA", indexes = @Index(name = "IDX_ALERTA_SENSOR", columnList = "SENSOR_ID"))
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SENSOR_ID", nullable = false, length = 50)
    private String sensorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_SENSOR", nullable = false, length = 30)
    private TipoSensor tipoSensor;

    @Enumerated(EnumType.STRING)
    @Column(name = "NIVEL", nullable = false, length = 10)
    private NivelAlerta nivel;

    @Column(name = "VALOR_PREVISTO", nullable = false)
    private double valorPrevisto;

    @Column(name = "LIMITE_ESTOURADO", nullable = false)
    private double limiteEstourado;

    @Column(name = "ESTRATEGIA", nullable = false, length = 60)
    private String estrategia;

    @Column(name = "MENSAGEM", nullable = false, length = 600)
    private String mensagem;

    @Column(name = "DATA_HORA", nullable = false)
    private LocalDateTime dataHora;

    protected Alerta() {
    }

    public Alerta(String sensorId, TipoSensor tipoSensor, NivelAlerta nivel, double valorPrevisto,
                  double limiteEstourado, String estrategia, String mensagem)
            throws DadosInvalidosException {
        if (sensorId == null || sensorId.isBlank() || tipoSensor == null) {
            throw new DadosInvalidosException("O alerta precisa informar sensor e tipo de sensor.");
        }
        if (nivel == null || nivel == NivelAlerta.NORMAL) {
            throw new DadosInvalidosException("Alerta exige nivel ATENCAO, ALERTA ou CRITICO.");
        }
        this.sensorId = sensorId.trim();
        this.tipoSensor = tipoSensor;
        this.nivel = nivel;
        this.valorPrevisto = valorPrevisto;
        this.limiteEstourado = limiteEstourado;
        this.estrategia = estrategia;
        this.mensagem = mensagem;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public TipoSensor getTipoSensor() {
        return tipoSensor;
    }

    public NivelAlerta getNivel() {
        return nivel;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public double getLimiteEstourado() {
        return limiteEstourado;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}