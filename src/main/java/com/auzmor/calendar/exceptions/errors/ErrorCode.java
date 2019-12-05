package com.auzmor.calendar.exceptions.errors;

import io.swagger.annotations.ApiModelProperty;

public enum ErrorCode {
  @ApiModelProperty(notes = "Not Found")
  E3000004,
  @ApiModelProperty(notes = "slot is not available")
  E0000000,
}
