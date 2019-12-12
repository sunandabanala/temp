package com.auzmor.calendar.services.impls;

import com.auzmor.calendar.services.ApplicationContextService;
import org.springframework.stereotype.Service;

public class ApplicationContextServiceImpl implements ApplicationContextService {

  private String email;
  private String token;
  private String username;
  private String currentUserId;
  private String defaultToken;

  @Override
  public String getCurrentUserEmail() {
    return this.email;
  }

  @Override
  public void setCurrentUserEmail(String email) {
    this.email=email;
  }

  @Override
  public String getCurrentUsername() {
    return this.username;
  }

  @Override
  public void setCurrentUsername(String name) {
    this.username=name;
  }

  @Override
  public String geToken() {
    return this.token;
  }

  @Override
  public void setToken(String token) {
    this.token=token;
  }

  @Override
  public String getCurrentUserId() {
    return this.currentUserId;
  }

  @Override
  public void setCurrentUserId(String userId) {
    this.currentUserId=userId;
  }

  @Override
  public String getDefaultToken() {
    return this.defaultToken;
  }

  @Override
  public void setDefaultToken(String defaultToken) {
    this.defaultToken=defaultToken;
  }
}
