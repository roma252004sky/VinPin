package ru.vinpin.vinpingateway;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/login", "/static/**").permitAll()
                        .anyExchange().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .authenticationFailureHandler((exchange, ex) -> {
                            exchange.getExchange().getSession().subscribe(session -> {
                                session.getAttributes().put("error", "Неверные логин или пароль");
                            });
                            exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                            exchange.getExchange().getResponse().getHeaders().setLocation(URI.create("/login?error"));
                            return exchange.getExchange().getResponse().setComplete();
                        })
                        .authenticationSuccessHandler((exchange, auth) -> {
                            exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                            exchange.getExchange().getResponse().getHeaders().setLocation(URI.create("/vinpin/front"));
                            return exchange.getExchange().getResponse().setComplete();
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((exchange, auth) -> {
                            exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                            exchange.getExchange().getResponse().getHeaders().setLocation(URI.create("/login"));
                            return exchange.getExchange().getResponse().setComplete();
                        })
                )
                .headers(headers->{
                    headers.frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable);
                })
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

}
