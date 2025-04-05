package ru.vinpin;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {
    @GetMapping("/main")
    public ResponseEntity<?> index(@RequestParam String name) {
        return ResponseEntity.ok("Hello " + name);
    }
}
