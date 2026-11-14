package br.com.fiap.territory.servico;

import br.com.fiap.territory.dominio.EstrategiaPrevisao;
import java.util.Arrays;
import java.util.List;

public final class FabricaPrevisao {
    private FabricaPrevisao() {
    }

    public static EstrategiaPrevisao criar(TipoPrevisao tipo, ConfiguracaoSistema configuracao) {
        return tipo.criar(configuracao);
    }

    public static List<EstrategiaPrevisao> criarTodas(ConfiguracaoSistema configuracao) {
        return Arrays.stream(TipoPrevisao.values()).map(tipo -> tipo.criar(configuracao)).toList();
    }
}
