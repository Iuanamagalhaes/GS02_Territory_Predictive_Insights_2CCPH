package br.com.fiap.territory.servico;

import br.com.fiap.territory.dominio.EstrategiaPrevisao;
import br.com.fiap.territory.dominio.PrevisaoComparacao;
import br.com.fiap.territory.dominio.PrevisaoMediaMovel;

import java.util.function.Function;

public enum TipoPrevisao {
    MEDIA_MOVEL(cfg -> new PrevisaoMediaMovel(cfg.getJanelaMediaMovel(), cfg.getMinimoLeituras())),
    COMPARACAO(cfg -> new PrevisaoComparacao(cfg.getJanelaComparacao(), cfg.getMinimoLeituras()));

    private final Function<ConfiguracaoSistema, EstrategiaPrevisao> criador;

    TipoPrevisao(Function<ConfiguracaoSistema, EstrategiaPrevisao> criador) {
        this.criador = criador;
    }

    EstrategiaPrevisao criar(ConfiguracaoSistema configuracao) {
        return criador.apply(configuracao);
    }
}
