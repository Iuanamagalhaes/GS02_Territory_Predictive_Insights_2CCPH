package br.com.fiap.territory.dominio;

import br.com.fiap.territory.excecao.FonteIndisponivelException;

public interface Sensor {
    String getId();
    String getNome();
    String getRegiao();
    TipoSensor getTipo();
    double lerValor() throws FonteIndisponivelException;
    boolean estaFuncionando();
    StatusComponente verificarStatus();
    void simularFalha(boolean falha);
}
