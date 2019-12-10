package com.auzmor.calendar.services;

import org.springframework.stereotype.Service;

public interface ApplicationContextService {

  String getCurrentUserEmail();
  void setCurrentUserEmail(String email);
  String geToken();
  void setToken(String email);
}
