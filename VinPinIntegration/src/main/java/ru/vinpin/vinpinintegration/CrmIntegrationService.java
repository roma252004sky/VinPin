package ru.vinpin.vinpinintegration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CrmIntegrationService {
    @Autowired
    private R2dbcEntityTemplate r2dbcEntityTemplate;
    private final WebClient webClient = WebClient.create("/vinpin/crm");
    record CrmDto(String info,
            String jsonStats){
    }
    @Scheduled(fixedRate = 3600*24)
    private void crmUpload(){
        webClient.get().uri("/crm").exchangeToFlux(
                clientResponse -> clientResponse.bodyToFlux(CrmDto.class)
        ).subscribe(crmDto -> {
            r2dbcEntityTemplate.insert(crmDto);
        }).dispose();
    }

}
