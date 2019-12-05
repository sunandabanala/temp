package com.auzmor.calendar.controllers.requests.events;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Getter
@Setter
public class SlotCheckRequest {

  @ApiModelProperty(notes="Start time/date of the event.", required = true, example = "2018-12-17T16:00:00+05:30[Asia/Kolkata]")
  @NotNull(message = "start date/time cannot be null.")
  //TODO add custom validators to ZonedDateTime
  private String start;

  @ApiModelProperty(notes="End time/date of the event.", required = true, example = "2018-12-17T16:30:00+05:30[Asia/Kolkata]")
  @NotNull(message = "end date/time cannot be null.")
  private String end;

  @ApiModelProperty(notes="End time/date of the event.", required = true, example = "abc@gmail.com")
  @NotNull(message = "end date/time cannot be null.")
  private String email;

  public long getStart() {
    return ZonedDateTime.parse(this.start).toInstant().getEpochSecond();

  }

  public long getEnd() {
    return ZonedDateTime.parse(this.end).toInstant().getEpochSecond();
  }
}
