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
  @NotNull(message = "Title cannot be null.")
  private String email;

  @ApiModelProperty(notes="Title of event", required = true, example = "StandUp")
  @NotNull(message = "Title cannot be null.")
  private String id;

}
