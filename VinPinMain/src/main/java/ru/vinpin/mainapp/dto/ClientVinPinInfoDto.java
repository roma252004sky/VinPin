package ru.vinpin.mainapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientVinPinInfoDto {
    private String fio;
    private String phone;
    private String enterprise;
    private Date dateStart;
    private String description;
    private String triggers;
    public ClientVinPinInfoDto(String fio, String phone, Date dateStart,String enterprise,String description, String triggers) {
        this.fio = fio;
        this.phone = phone;
        this.enterprise = enterprise;
        this.dateStart = dateStart;
        this.description = description;
        this.triggers = triggers;
    }
}
