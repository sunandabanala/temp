package com.auzmor.calendar.interceptors;

import com.auzmor.calendar.configurations.auth.CustomPrincipal;
import com.auzmor.calendar.exceptions.DBException;
import com.auzmor.calendar.services.ApplicationContextService;
import org.springframework.beans.factory.annotation.Autowired;
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
    applicationContextService.setCurrentUserEmail(email);
    return true;
  }
}
