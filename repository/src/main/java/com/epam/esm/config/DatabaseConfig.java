package com.epam.esm.config;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.mapper.GiftCertificateBeanPropertyRowMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:db_${spring.profiles.active}.properties")
@ComponentScan("com.epam.esm")
@EnableTransactionManagement
public class DatabaseConfig {

  @Value("${db.driver}")
  private String driverClassName;

  @Value("${db.url}")
  private String url;

  @Value("${db.username}")
  private String username;

  @Value("${db.password}")
  private String password;

  @Value("${db.max_pool_size}")
  private int maxPoolSize;

  @Bean
  public DataSource dataSource() {
    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setDriverClassName(driverClassName);
    hikariConfig.setJdbcUrl(url);
    hikariConfig.setUsername(username);
    hikariConfig.setPassword(password);
    hikariConfig.setMaximumPoolSize(maxPoolSize);
    return new HikariDataSource(hikariConfig);
  }

  @Bean
  public PlatformTransactionManager transactionManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  public BeanPropertyRowMapper<Tag> tagRowMapper() {
    return new BeanPropertyRowMapper<>(Tag.class);
  }

  @Bean
  public GiftCertificateBeanPropertyRowMapper giftCertificateRowMapper() {
    GiftCertificateBeanPropertyRowMapper rowMapper = new GiftCertificateBeanPropertyRowMapper();
    rowMapper.setMappedClass(GiftCertificate.class);
    return rowMapper;
  }
}
