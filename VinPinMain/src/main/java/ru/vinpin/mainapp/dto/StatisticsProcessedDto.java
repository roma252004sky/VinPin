package ru.vinpin.mainapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsProcessedDto {

    @JsonProperty("client_id")
    private Integer clientId;

    @JsonProperty("reasons")
    private List<String> reasons;

    @JsonProperty("recommendations")
    private List<String> recommendations;

    @JsonProperty("risk_level")
    private String riskLevel;

    @JsonProperty("risk_score")
    private Double riskScore;

    @JsonProperty("triggers")
    private List<String> triggers;

}
