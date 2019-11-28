package com.auzmor.calendar.configurations.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomOrganization {

  private String uuid;
  private String name;
  private String domain;
  private String url;
  private String favicon;
  private String logo;

}
