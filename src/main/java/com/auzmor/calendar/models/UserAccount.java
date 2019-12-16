package com.auzmor.calendar.models;

import com.auzmor.calendar.models.entities.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserAccount extends Entity {
  private String username;
  private String userId;
  private String providerType;
  private String refreshToken;
  private String nylasToken;
  private String nylasAccountId;
  private String cursorId;
  private String uuid;

}
