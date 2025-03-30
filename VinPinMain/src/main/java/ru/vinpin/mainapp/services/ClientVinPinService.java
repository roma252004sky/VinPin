package ru.vinpin.mainapp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.vinpin.mainapp.dto.ClientVinPinDto;
import ru.vinpin.mainapp.dto.ClientVinPinInfoDto;
import ru.vinpin.mainapp.dto.StatisticsDto;
import ru.vinpin.mainapp.dto.StatisticsProcessedDto;

import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ClientVinPinService {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    public List<ClientVinPinDto> getClients() {
        // 1. Используем RowMapper для автоматического маппинга
        return namedParameterJdbcTemplate.query("""
        SELECT 
            c.id,
            c.fio,
            s.risk_level,
            s.risk_score,
            s.reason,
            s.recommendation
        FROM vin_pin.clients c
        JOIN (
            SELECT 
                fk_client,
                risk_level,
                risk_score,
                reason,
                recommendation,
                ROW_NUMBER() OVER(PARTITION BY fk_client ORDER BY date_create DESC) as rn
            FROM vin_pin.statistics
        ) s ON c.id = s.fk_client AND s.rn = 1
        """,
                new MapSqlParameterSource(),
                new BeanPropertyRowMapper<>(ClientVinPinDto.class) {
                    @Override
                    public ClientVinPinDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                        ClientVinPinDto dto = new ClientVinPinDto();
                        dto.setId(rs.getInt("id"));
                        dto.setFio(rs.getString("fio"));
                        dto.setRisk(getRisk(rs.getString("risk_level")));
                        dto.setRiskScore(rs.getObject("risk_score", Double.class));
                        dto.setReason(rs.getString("reason"));
                        dto.setRecommendation(rs.getString("recommendation"));
                        return dto;
                    }
                });
    }
    public ClientVinPinInfoDto getClientInfo(int id){
        String sql = """
            SELECT fio, phone, date_start, description, triggers, enterprise\s
            FROM vin_pin.clients
            JOIN vin_pin.statistics s on clients.id = s.fk_client
            WHERE clients.id = :id
           \s""";

        Map<String, Object> params = Map.of("id", id);

        return namedParameterJdbcTemplate.queryForObject(
                sql,
                params,
                (rs, rowNum) -> Optional.of(new ClientVinPinInfoDto(
                        rs.getString("fio"),
                        rs.getString("phone"),
                        rs.getDate("date_start"),
                        rs.getString("enterprise"),
                        rs.getString("description"),
                        rs.getString("triggers")
                ))
        ).get();
    }

    public void refreshPredictForClients() throws JsonProcessingException {
        final int BATCH_SIZE = 100000;
        ObjectMapper mapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();

        // 1. Пакетное чтение данных
        int offset = 0;
        while (true) {
            // 1.1. Чтение данных пачками
            List<StatisticsDto> batch = new ArrayList<>(BATCH_SIZE);
            SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(
                    """
                    SELECT 
                        days_since_last_login,
                        avg_catalogs_opened,
                        products_in_tariff,
                        manager_interactions,
                        avg_time_in_product,
                        total_products_purchased,
                        support_satisfaction,
                        days_until_sub_end,
                        id
                    FROM vin_pin.statistics
                    ORDER BY id
                    LIMIT :limit OFFSET :offset""",
                    new MapSqlParameterSource()
                            .addValue("limit", BATCH_SIZE)
                            .addValue("offset", offset)
            );

            // 1.2. Преобразование результатов
            while (rs.next()) {
                StatisticsDto s = StatisticsDto.builder()
                        .clientId(rs.getInt("id"))
                        .daysSinceLastLogin(rs.getInt("days_since_last_login"))
                        .avgCatalogsOpened(rs.getDouble("avg_catalogs_opened"))
                        .productsInTariff(rs.getInt("products_in_tariff"))
                        .managerInteractions(rs.getInt("manager_interactions"))
                        .avgTimeInProduct(rs.getInt("avg_time_in_product"))
                        .totalProductsPurchased(rs.getInt("total_products_purchased"))
                        .supportSatisfaction(rs.getDouble("support_satisfaction"))
                        .daysUntilSubEnd(rs.getInt("days_until_sub_end"))
                        .build();
                batch.add(s);
            }

            if (batch.isEmpty()) break;

            // 2. Пакетная обработка ML
            processBatchWithML(mapper, restTemplate, batch);
            offset += BATCH_SIZE;
            System.out.println("offset: " + offset);
        }
    }

    private void processBatchWithML(ObjectMapper mapper,
                                    RestTemplate restTemplate,
                                    List<StatisticsDto> batch) throws JsonProcessingException {
        // 2.1. Отправка запроса к ML-сервису
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(
                mapper.writeValueAsString(batch),
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:5000/vinpin/ml/predict",
                HttpMethod.POST,
                request,
                String.class
        );

        // 2.2. Парсинг ответа
        List<StatisticsProcessedDto> processedBatch = mapper.readValue(
                response.getBody(),
                new TypeReference<>() {}
        );

        // 3. Пакетное обновление данных
        updateInDatabase(processedBatch);
    }

    private void updateInDatabase(List<StatisticsProcessedDto> batch) {
        // 3.1. Подготовка параметров
        List<MapSqlParameterSource> paramsList = new ArrayList<>(batch.size());

        for (StatisticsProcessedDto dto : batch) {
            paramsList.add(new MapSqlParameterSource()
                    .addValue("id", dto.getClientId())
                    .addValue("risk_score", dto.getRiskScore())
                    .addValue("reasons", String.join(", ", dto.getReasons()))
                    .addValue("recommendation", String.join(", ", dto.getRecommendations()))
                    .addValue("risk_level", dto.getRiskLevel())
                    .addValue("triggers", String.join(", ", dto.getTriggers())));
        }

        // 3.2. Пакетное выполнение запросов
        namedParameterJdbcTemplate.batchUpdate(
                """
                UPDATE vin_pin.statistics_full
                SET 
                    risk_score = :risk_score,
                    reason = :reasons,
                    recommendation = :recommendation,
                    risk_level = :risk_level,
                    triggers = :triggers
                WHERE id = :id""",
                paramsList.toArray(new MapSqlParameterSource[0])
        );
    }
    public String getRisk(String risk){
        return switch (risk) {
            case "high_risk" -> "Высокий уровень риска";
            case "medium_risk" -> "Средний уровень риска";
            case "low_risk" -> "Низкий уровень риска";
            default -> "Минимальный уровень риска";
        };
    }

}
