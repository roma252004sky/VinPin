package ru.vinpin.mainapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DbConfig {
    @Autowired
    private Environment env;
    @Bean
    public DataSource dataSource() {
        DataSourceBuilder<?> dataSource = DataSourceBuilder.create();
        dataSource.url(env.getProperty("vinpin.datasource.url"));
        dataSource.username(env.getProperty("vinpin.datasource.username"));
        dataSource.password(env.getProperty("vinpin.datasource.password"));
        dataSource.driverClassName("org.postgresql.Driver");
        return dataSource.build();
    }
    @Bean
    @Qualifier("jdbcTemplateCustom")
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
