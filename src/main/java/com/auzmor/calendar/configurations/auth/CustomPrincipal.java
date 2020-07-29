package com.auzmor.calendar.configurations.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomPrincipal implements Serializable {

  private static final long serialVersionUID = 1L;

  private String uuid;
  private String firstName;
  private String lastName;
  private String email;
  private String country;
  private String mobile;
  private String type;
  private CustomOrganization organization;
  private Set<CustomRole> roles;
  private String nylasToken;
  private Set<String> permissions;

}
