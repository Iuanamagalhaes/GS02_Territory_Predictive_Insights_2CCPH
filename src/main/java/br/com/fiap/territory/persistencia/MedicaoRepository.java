package br.com.fiap.territory.persistencia;

import br.com.fiap.territory.dominio.Medicao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicaoRepository extends JpaRepository<Medicao, Long> {
    List<Medicao> findBySensorIdOrderByIdAsc(String sensorId);

    List<Medicao> findTop20BySensorIdOrderByIdDesc(String sensorId);

    long countBySensorId(String sensorId);
}
