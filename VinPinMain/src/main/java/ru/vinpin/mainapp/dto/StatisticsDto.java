package ru.vinpin.mainapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDto {
    @JsonProperty("client_id")
    private Integer clientId;  // Было fkClient, но в JSON просто "client_id"

    @JsonProperty("days_since_last_login")
    private Integer daysSinceLastLogin;

    @JsonProperty("avg_catalogs_opened")
    private Double avgCatalogsOpened;  // В JSON 0.8 (Double, а не Integer)

    @JsonProperty("products_in_tariff")
    private Integer productsInTariff;

    @JsonProperty("manager_interactions")  // В JSON "manager_interactions" (опечатка?)
    private Integer managerInteractions;

    @JsonProperty("avg_time_in_product")
    private Integer avgTimeInProduct;

    @JsonProperty("total_products_purchased")
    private Integer totalProductsPurchased;

    @JsonProperty("support_satisfaction")
    private Double supportSatisfaction;  // В JSON 1.5 (Double, а не Integer)

    @JsonProperty("days_until_sub_end")
    private Integer daysUntilSubEnd;
}