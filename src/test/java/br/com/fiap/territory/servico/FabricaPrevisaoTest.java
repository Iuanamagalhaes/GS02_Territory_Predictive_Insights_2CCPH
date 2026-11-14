package br.com.fiap.territory.servico;

import br.com.fiap.territory.dominio.EstrategiaPrevisao;
import br.com.fiap.territory.dominio.PrevisaoComparacao;
import br.com.fiap.territory.dominio.PrevisaoMediaMovel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class FabricaPrevisaoTest {
    private ConfiguracaoSistema configuracao;

    @BeforeEach
    void setUp() {
        configuracao = ConfiguracaoSistema.getInstancia();
        configuracao.resetarPadrao();
    }

    @Test
    void criaAEstrategiaCorrespondenteAoTipo() {
        EstrategiaPrevisao media = FabricaPrevisao.criar(TipoPrevisao.MEDIA_MOVEL, configuracao);
        EstrategiaPrevisao comparacao = FabricaPrevisao.criar(TipoPrevisao.COMPARACAO, configuracao);
        assertInstanceOf(PrevisaoMediaMovel.class, media);
        assertInstanceOf(PrevisaoComparacao.class, comparacao);
    }

    @Test
    void criaTodasAsEstrategiasDisponiveis() {
        List<EstrategiaPrevisao> todas = FabricaPrevisao.criarTodas(configuracao);
        assertEquals(TipoPrevisao.values().length, todas.size());
    }

    @Test
    void configuracaoEUmaUnicaInstancia() {
        assertEquals(ConfiguracaoSistema.getInstancia(), ConfiguracaoSistema.getInstancia());
    }
}
