package com.auzmor.calendar.services.impls;

import com.auzmor.calendar.services.ApplicationContextService;
import org.springframework.stereotype.Service;

public class ApplicationContextServiceImpl implements ApplicationContextService {

  private String token;
  private String username;
  private String currentUserId;
  private String defaultToken;
  private String accountId;
  private String defaultAccountId;
  private String defaultUserId;
  private String providerType;
  private String providerRefreshToken;
  private String email;

  @Override
  public String getAccountId() {
    return this.accountId;
  }

  @Override
  public void setAccountId(String accountId) {
    this.accountId=accountId;
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

  @Override
  public String getDefaultAccountId() {
    return this.defaultAccountId;
  }

  @Override
  public void setDefaultAccountId(String defaultAccountId) {
    this.defaultAccountId=defaultAccountId;
  }

  @Override
  public String getDefaultUserId() {
    return this.defaultUserId;
  }

  @Override
  public void setDefaultUserId(String defaultUserId) {
    this.defaultUserId=defaultUserId;
  }

  @Override
  public String getProviderType() {
    return this.providerType;
  }

  @Override
  public void setProviderType(String providerType) {
    this.providerType = providerType;
  }

  @Override
  public String getProviderRefreshToken() {
    return this.providerRefreshToken;
  }

  @Override
  public void setProviderRefreshToken(String providerRefreshToken) {
    this.providerRefreshToken = providerRefreshToken;
  }

  @Override
  public String getEmail() {
    return this.email;
  }

  @Override
  public void setEmail(String email) {
    this.email = email;
  }

}
