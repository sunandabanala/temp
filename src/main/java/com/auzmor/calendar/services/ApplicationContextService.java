package com.auzmor.calendar.services;


public interface ApplicationContextService {

  String getAccountId();
  void setAccountId(String name);
  String getCurrentUsername();
  void setCurrentUsername(String name);
  String geToken();
  void setToken(String email);
  String getCurrentUserId();
  void setCurrentUserId(String userId);
  String getDefaultToken();
  void setDefaultToken(String defaultToken);
  String getDefaultAccountId();
  void setDefaultAccountId(String name);
  String getDefaultUserId();
  void setDefaultUserId(String userId);
  String getProviderType();
  void setProviderType(String providerType);
  String getProviderRefreshToken();
  void setProviderRefreshToken(String refreshToken);
  String getEmail();
  void setEmail(String email);
}
