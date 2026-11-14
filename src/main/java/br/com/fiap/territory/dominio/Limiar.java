package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.DadosInvalidosException;
import java.util.Locale;

public final class Limiar {

    private final double atencao;
    private final double alerta;
    private final double critico;

    public Limiar(TipoSensor tipo, double atencao, double alerta, double critico)
            throws DadosInvalidosException {
        if (tipo == null) {
            throw new DadosInvalidosException("Limiar inválido: tipo de sensor não informado.");
        }
        if (!tipo.estaNaFaixa(atencao) || !tipo.estaNaFaixa(alerta) || !tipo.estaNaFaixa(critico)) {
            throw new DadosInvalidosException(String.format(Locale.US,
                    "Limiar inválido para %s: os valores devem estar entre %.1f e %.1f %s.",
                    tipo.getDescricao().toLowerCase(Locale.ROOT), tipo.getMinimo(), tipo.getMaximo(),
                    tipo.getUnidade()));
        }
        if (!(atencao < alerta && alerta < critico)) {
            throw new DadosInvalidosException(String.format(Locale.US,
                    "Limiar inválido: exige atenção < alerta < crítico (recebido %.2f / %.2f / %.2f).",
                    atencao, alerta, critico));
        }
        this.atencao = atencao;
        this.alerta = alerta;
        this.critico = critico;
    }

    public double getAtencao() {
        return atencao;
    }

    public double getAlerta() {
        return alerta;
    }

    public double getCritico() {
        return critico;
    }

    public NivelAlerta classificar(double valor) {
        if (valor >= critico) return NivelAlerta.CRITICO;
        if (valor >= alerta) return NivelAlerta.ALERTA;
        if (valor >= atencao) return NivelAlerta.ATENCAO;
        return NivelAlerta.NORMAL;
    }

    public double limiteDo(NivelAlerta nivel) {
        return switch (nivel) {
            case CRITICO -> critico;
            case ALERTA -> alerta;
            case ATENCAO, NORMAL -> atencao;
        };
    }
}
