package com.auzmor.calendar.services.impls;

import com.auzmor.calendar.services.ApplicationContextService;
import org.springframework.stereotype.Service;

public class ApplicationContextServiceImpl implements ApplicationContextService {

  private String email;
  private String token;

  @Override
  public String getCurrentUserEmail() {
    return this.email;
  }

  @Override
  public void setCurrentUserEmail(String email) {
    this.email=email;
  }

  @Override
  public String geToken() {
    return this.token;
  }

  @Override
  public void setToken(String token) {
    this.token=token;
  }
}
