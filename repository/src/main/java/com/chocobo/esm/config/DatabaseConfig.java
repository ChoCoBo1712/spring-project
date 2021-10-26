package com.chocobo.esm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName("org.postgresql.Drive");
        driverManagerDataSource.setUrl("postgres://ddghlknj:90zioIG8JmI5aHQsg9RWPL6EUVpwoDK5@hattie.db.elephantsql.com/ddghlknj");
        driverManagerDataSource.setUsername("ddghlknj");
        driverManagerDataSource.setPassword("90zioIG8JmI5aHQsg9RWPL6EUVpwoDK5");
        return driverManagerDataSource;
    }
}
