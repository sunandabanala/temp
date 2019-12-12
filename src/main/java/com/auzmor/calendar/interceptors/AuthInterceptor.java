package com.auzmor.calendar.interceptors;

import com.auzmor.calendar.configurations.auth.CustomPrincipal;
import com.auzmor.calendar.exceptions.DBException;
import com.auzmor.calendar.mappers.CalendarMapper;
import com.auzmor.calendar.services.ApplicationContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

  @Autowired
  private ApplicationContextService applicationContextService;

  @Autowired
  CalendarMapper calendarMapper;

  @Override
  public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response, Object object) throws DBException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
      return true;
    }
    OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
    CustomPrincipal customPrincipal = (CustomPrincipal) oAuth2Authentication.getUserAuthentication().getPrincipal();
    final String email = customPrincipal.getEmail();
    final String nylasToken  = customPrincipal.getNylasToken();
    final String username = customPrincipal.getFirstName()+" "+customPrincipal.getLastName();
    final String userId = customPrincipal.getUuid();
    String defaultToken=calendarMapper.getTokenByEmail(System.getenv("default_email"));
    if(nylasToken != null) {
      applicationContextService.setToken(nylasToken);
    }else{
      String token = calendarMapper.getTokenByUserId(userId);
      if(token == null) {
        token = defaultToken;
        applicationContextService.setCurrentUserEmail(System.getenv("default_email"));
      }else{
        applicationContextService.setCurrentUserEmail(email);
      }
      applicationContextService.setToken(token);
    }
    applicationContextService.setCurrentUsername(username);
    applicationContextService.setCurrentUserId(userId);
    applicationContextService.setDefaultToken(defaultToken);

    return true;
  }
}
