package com.auzmor.calendar.configurations.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomRole {

  private String uuid;
  private String name;
  private Set<CustomPermission> permissions;
}
