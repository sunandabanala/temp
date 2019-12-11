package com.auzmor.calendar.services;

import org.springframework.stereotype.Service;

public interface ApplicationContextService {

  String getCurrentUserEmail();
  void setCurrentUserEmail(String name);
  String getCurrentUsername();
  void setCurrentUsername(String name);
  String geToken();
  void setToken(String email);
}
