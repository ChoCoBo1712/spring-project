package com.chocobo.esm.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    private static HikariConfig hikariConfig = new HikariConfig();

    @Bean
    public DataSource dataSource() {
        hikariConfig.setDriverClassName("org.postgresql.Drive");
        hikariConfig.setJdbcUrl( "postgres://ddghlknj:90zioIG8JmI5aHQsg9RWPL6EUVpwoDK5@hattie.db.elephantsql.com/ddghlknj" );
        hikariConfig.setUsername( "ddghlknj" );
        hikariConfig.setPassword( "90zioIG8JmI5aHQsg9RWPL6EUVpwoDK5" );
        hikariConfig.setMaximumPoolSize(5);
        return new HikariDataSource(hikariConfig);
    }
}
