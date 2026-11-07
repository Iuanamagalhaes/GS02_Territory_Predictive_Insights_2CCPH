-- Medicoes de um sensor, da mais recente para a mais antiga
SELECT * FROM MEDICAO WHERE SENSOR_ID = 'TEMP-ZL-01' ORDER BY ID DESC;

-- Quantidade de medicoes por sensor
SELECT SENSOR_ID, COUNT(*) AS TOTAL FROM MEDICAO GROUP BY SENSOR_ID;

-- Alertas emitidos, do mais recente para o mais antigo
SELECT ID, DATA_HORA, NIVEL, SENSOR_ID, VALOR_PREVISTO, LIMITE_ESTOURADO, ESTRATEGIA
FROM ALERTA ORDER BY ID DESC;

-- Alertas por nivel
SELECT NIVEL, COUNT(*) AS TOTAL FROM ALERTA GROUP BY NIVEL;
