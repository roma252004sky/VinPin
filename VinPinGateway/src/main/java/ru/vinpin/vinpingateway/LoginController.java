package ru.vinpin.vinpingateway;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class LoginController {

    @GetMapping("/login")
    public Mono<String> loginPage(@RequestParam(required = false) String error,
                                  ServerWebExchange exchange) {
        return exchange.getSession()
                .flatMap(session -> {
                    String errorMessage = (String) session.getAttributes().get("error");
                    if (errorMessage != null) {
                        session.getAttributes().remove("error");
                        return Mono.just("redirect:/login?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
                    }
                    session.getAttributes().remove("error");
                    return Mono.just("login");
                });
    }


    @GetMapping("/")
    public Mono<Void> homePage(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
        exchange.getResponse().getHeaders().setLocation(URI.create("/vinpin/front"));
        return exchange.getResponse().setComplete();
    }
}