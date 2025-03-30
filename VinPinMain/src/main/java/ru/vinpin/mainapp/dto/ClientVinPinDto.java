package ru.vinpin.mainapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientVinPinDto {
    private int id;
    private String fio;
    private String risk;
    private Double riskScore;
    private String reason;
    private String recommendation;
}
