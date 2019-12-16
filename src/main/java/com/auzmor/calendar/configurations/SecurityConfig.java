package com.auzmor.calendar.configurations;

import com.auzmor.calendar.configurations.auth.CustomPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().and().csrf().disable()
      .authorizeRequests()
      .antMatchers( "/v2/api-docs", "swagger-ui.html", "/organization", "/webhook").permitAll();
    // .and()
    // .httpBasic();
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    /*
    web
        .ignoring()
        .antMatchers("/organization").anyRequest()
    ; // #3
     */
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    argumentResolvers.add(currentUserHandlerMethodArgumentResolver());
  }

  @Bean
  public HandlerMethodArgumentResolver currentUserHandlerMethodArgumentResolver() {
    return new HandlerMethodArgumentResolver() {
      @Override
      public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().equals(CustomPrincipal.class);
      }

      @Override
      public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        try {
          return (CustomPrincipal) SecurityContextHolder.getContext().getAuthentication();
        } catch (Exception e) {
          return null;
        }
      }
    };
  }
}
