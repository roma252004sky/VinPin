package ru.vinpin.mainapp.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.vinpin.mainapp.dto.ClientVinPinDto;
import ru.vinpin.mainapp.dto.ClientVinPinInfoDto;
import ru.vinpin.mainapp.services.ClientVinPinService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/vinpin/main")
public class ClientController {
    private final ClientVinPinService clientService;
    @GetMapping("/getClients")
    public List<ClientVinPinDto> getClients() {
        return clientService.getClients();
    }
    @GetMapping("/getClientInfo")
    public ClientVinPinInfoDto getClientInfo(@RequestParam int id) {
        return clientService.getClientInfo(id);
    }
    @GetMapping("/refreshPredictions")
    public ResponseEntity<?> refreshPredictions() throws JsonProcessingException {
        clientService.refreshPredictForClients();
        return ResponseEntity.ok().build();
    }
}
