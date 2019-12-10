package com.auzmor.calendar.models;

import com.auzmor.calendar.models.entities.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Account extends Entity {
  private String userName;
  private String providerName;
  private String refreshToken;
  private String nylasToken;
  private String nylasAccountId;
  private String cursorId;
  private String uuid;

  public Account(String uuid, String userName, String providerName, String refreshToken, String nylasToken, String nylasAccountId, String cursorId) {
    this.uuid = uuid;
    this.cursorId = cursorId;
    this.userName = userName;
    this.providerName = providerName;
    this.refreshToken = refreshToken;
    this.nylasToken = nylasToken;
    this.nylasAccountId = nylasAccountId;
  }
}
