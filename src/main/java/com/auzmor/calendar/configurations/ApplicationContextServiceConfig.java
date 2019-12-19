package com.auzmor.calendar.configurations;

import com.auzmor.calendar.services.ApplicationContextService;
import com.auzmor.calendar.services.impls.ApplicationContextServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Configuration
public class ApplicationContextServiceConfig {

  @Bean
  @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
  public ApplicationContextService userService() {
    return new ApplicationContextServiceImpl();
  }
}
