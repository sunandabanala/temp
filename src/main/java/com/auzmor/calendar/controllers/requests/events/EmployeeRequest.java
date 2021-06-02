package com.auzmor.calendar.controllers.requests.events;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description="Employee request object.")
public class EmployeeRequest {
  private String lastName;
  private String firstName;
  private String uuid;
  private String createdAt;
  private String updatedAt;
  private String email;
}
