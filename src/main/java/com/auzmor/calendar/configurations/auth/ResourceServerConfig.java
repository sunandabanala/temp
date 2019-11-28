package com.auzmor.calendar.configurations.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

  @Value("${spring.oauth2.resource.jwt.key-value}")
  private String publicKey;
  private String resourceIds = "oauth2-resource";
  @Autowired
  private CustomAccessTokenConverter customAccessTokenConverter;

  @Bean
  public TokenStore tokenStore() {
    return new JwtTokenStore(accessTokenConverter());
  }

  @Bean
  public JwtAccessTokenConverter accessTokenConverter() {
    //CustomAccessTokenConverter converter = new CustomAccessTokenConverter();
    JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    converter.setAccessTokenConverter(customAccessTokenConverter);
    converter.setVerifierKey(publicKey);
    return converter;
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http
//        .authorizeRequests()
//        .anyRequest()
//        .permitAll().
      .authorizeRequests()
      .antMatchers("/organization",
        "/logout.do",
        "/webjars/**",
        "/resources/**",
        "/error",
        "/js/**",
        "/css/**",
        "/img/**").permitAll()
      .and().anonymous().disable()
      .requestMatchers().antMatchers("/api/v1/**")
      .and().authorizeRequests()
      .and().cors().disable().csrf().disable().httpBasic().disable()
      .exceptionHandling()
      .authenticationEntryPoint(customAuthEntryPoint())
      //       (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
      .accessDeniedHandler(
        (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED));
  }

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    resources.resourceId(resourceIds).tokenStore(tokenStore())
      .authenticationEntryPoint(customAuthEntryPoint())
    ;
  }
  @Bean
  public AuthenticationEntryPoint customAuthEntryPoint() {
    return new CustomEntryPoint();
  }

}
