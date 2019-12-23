package com.auzmor.calendar.interceptors;

import com.auzmor.calendar.configurations.auth.CustomPrincipal;
import com.auzmor.calendar.exceptions.DBException;
import com.auzmor.calendar.exceptions.handlers.CalendarExceptionHandler;
import com.auzmor.calendar.mappers.CalendarMapper;
import com.auzmor.calendar.services.ApplicationContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.auzmor.calendar.constants.DataConstants.DEFAULT_MAIL;

@Component
public class AuthInterceptor implements HandlerInterceptor {

  private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
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
    String userNylasToken=null;
    String userAccountId=null;
    Map<String,String> defaultTokenDataByEmail=calendarMapper.getTokenDataByEmail(DEFAULT_MAIL);
    Map<String,String> userTokenDataByEmail = calendarMapper.getTokenDataByUserId(userId);
    System.out.println(userTokenDataByEmail);
    if(nylasToken != null) {
      logger.error("nylastoken: "+nylasToken);
      applicationContextService.setToken(nylasToken);
      applicationContextService.setAccountId(userTokenDataByEmail.get("uuid"));
    }else{
      logger.error("userTokenDataByEmail: "+userId);
      if(userTokenDataByEmail == null) {
        logger.error("userTokenDataByEmail first loop: "+userId);
        userNylasToken = defaultTokenDataByEmail.get("nylas_token");
        userAccountId = defaultTokenDataByEmail.get("uuid");
      }else {
        logger.error("userTokenDataByEmail second loop: ");
        userNylasToken = userTokenDataByEmail.get("nylas_token");
        userAccountId = userTokenDataByEmail.get("uuid");
      }
      applicationContextService.setToken(userNylasToken);
      applicationContextService.setAccountId(userAccountId);
    }
    applicationContextService.setCurrentUsername(username);
    applicationContextService.setCurrentUserId(userId);
    applicationContextService.setDefaultToken(defaultTokenDataByEmail.get("nylas_token"));
    applicationContextService.setDefaultAccountId(defaultTokenDataByEmail.get("uuid"));
    applicationContextService.setDefaultUserId(defaultTokenDataByEmail.get("user_id"));

    return true;
  }
}
