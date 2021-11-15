package com.epam.esm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringFoxConfig {

  @Bean
  public Docket swaggerCertificatesApi11() {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("certificates-api-v1.1")
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.regex("/api/v1.1.*"))
        .build()
        .apiInfo(
            new ApiInfoBuilder()
                .version("1.1")
                .title("Certificates API")
                .description("Documentation for Certificates API v1.1")
                .build());
  }

  @Bean
  public Docket swaggerCertificatesApi20() {
    return new Docket(DocumentationType.SWAGGER_2)
            .groupName("certificates-api-v2.0")
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.regex("/api/v2.0.*"))
            .build()
            .apiInfo(
                    new ApiInfoBuilder()
                            .version("2.0")
                            .title("Certificates API")
                            .description("Documentation for Certificates API v2.0")
                            .build());
  }
}
