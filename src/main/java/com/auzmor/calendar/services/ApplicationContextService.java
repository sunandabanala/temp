package com.auzmor.calendar.services;


public interface ApplicationContextService {

  String getCurrentUserEmail();
  void setCurrentUserEmail(String name);
  String getCurrentUsername();
  void setCurrentUsername(String name);
  String geToken();
  void setToken(String email);
  String getCurrentUserId();
  void setCurrentUserId(String userId);
  String getDefaultToken();
  void setDefaultToken(String defaultToken);
}
