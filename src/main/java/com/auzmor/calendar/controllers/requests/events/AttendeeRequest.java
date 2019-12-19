package com.auzmor.calendar.controllers.requests.events;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel(description="Request object to create a new Event.")
public class AttendeeRequest {

  @ApiModelProperty(notes="attendee email", required = true, example = "abc@auzmor.com")
  @NotNull(message = "abc@auzmor.com")
  private String email;

  @ApiModelProperty(notes="attendee uuid", required = true, example = "12345678912345678912345678912345")
  @NotNull(message = "12345678912345678912345678912345")
  private String id;

}
