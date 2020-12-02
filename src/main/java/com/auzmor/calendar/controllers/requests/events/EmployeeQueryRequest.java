package com.auzmor.calendar.controllers.requests.events;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description="Request object to create a new Event.")
public class EmployeeQueryRequest {

  private String lastName;
  private String firstName;
  private String uuid;
  private String createdAt;
  private String updatedAt;
  private String fullName;
  private String email;

}