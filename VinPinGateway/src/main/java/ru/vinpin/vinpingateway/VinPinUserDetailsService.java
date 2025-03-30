package ru.vinpin.vinpingateway;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.springframework.r2dbc.core.DatabaseClient;

@Service
public class VinPinUserDetailsService implements ReactiveUserDetailsService {

    private final DatabaseClient databaseClient;

    public VinPinUserDetailsService(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return databaseClient.sql("SELECT id, username, password, surname, name FROM vin_pin.users WHERE username = :username")
                .bind("username", username)
                .map((row, metadata) -> {
                    UserVinPin user = new UserVinPin();
                    user.setId(row.get("id", Integer.class));
                    user.setUsername(row.get("username", String.class));
                    user.setPassword(row.get("password", String.class));
                    user.setSurname(row.get("surname", String.class));
                    user.setName(row.get("name", String.class));
                    return user;
                })
                .one()
                .cast(UserDetails.class)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Неверные логин или пароль")));
    }
}
