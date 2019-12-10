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

  @ApiModelProperty(notes="Title of event", required = true, example = "StandUp")
  @NotNull(message = "abc@auzmor.com")
  private String email;

  @ApiModelProperty(notes="Title of event", required = true, example = "StandUp")
  @NotNull(message = "12345678912345678912345678912345")
  private String id;

}
