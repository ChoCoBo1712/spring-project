package com.chocobo.esm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebMvc
@ComponentScan("com.chocobo.esm")
public class WebConfig implements WebMvcConfigurer {

  private static final String ERRORS_BUNDLE_NAME = "i18n/errors";

  @Bean
  public ResourceBundleMessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename(ERRORS_BUNDLE_NAME);
    messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
    return messageSource;
  }
}
