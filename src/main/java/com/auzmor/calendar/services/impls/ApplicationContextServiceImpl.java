package com.auzmor.calendar.services.impls;

import com.auzmor.calendar.services.ApplicationContextService;
import org.springframework.stereotype.Service;

@Service
public class ApplicationContextServiceImpl implements ApplicationContextService {

  private String email;

  @Override
  public String getCurrentUserEmail() {
    return this.email;
  }

  @Override
  public void setCurrentUserEmail(String email) {
    this.email=email;
  }
}
