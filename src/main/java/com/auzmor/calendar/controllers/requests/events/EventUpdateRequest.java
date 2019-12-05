package com.auzmor.calendar.controllers.requests.events;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.Set;

@Getter
@Setter
@ApiModel(description="Request object to create a new Event.")
public class EventUpdateRequest {

  @ApiModelProperty(notes="Title of event", required = true, example = "StandUp")
  @NotNull(message = "Title cannot be null.")
  private String title;

  @ApiModelProperty(notes="Title of event", required = true, example = "abc@auzmor.com")
  @NotNull(message = "Title cannot be null.")
  private String username;

  @ApiModelProperty(notes="Start time/date of the event.", required = true, example = "2018-12-17T16:58:03.209657+05:30[Asia/Kolkata]")
  @NotNull(message = "start date/time cannot be null.")
  //TODO add custom validators to ZonedDateTime
  private String start;

  @ApiModelProperty(notes="End time/date of the event.", required = true, example = "2018-12-17T16:58:03.209657+05:30[Asia/Kolkata]")
  @NotNull(message = "end date/time cannot be null.")
  private String end;

  @ApiModelProperty(notes="All invitees for this event", example = "[abc@gmail.com]")
  private Set<AttendeeRequest> inviteeIds;

  @ApiModelProperty(notes="Title of event", required = true, example = "StandUp")
  private String description;

  @ApiModelProperty(notes="Title of event", required = true, example = "StandUp")
  private String location;

  @ApiModelProperty(notes="Title of event", required = true, example = "StandUp")
  @NotNull(message = "Title cannot be null.")
  private String type;

  @ApiModelProperty(notes="Title of event", required = true, example = "StandUp")
  private String url;

  public long getStart() {
    return ZonedDateTime.parse(this.start).toInstant().getEpochSecond();

  }

  public long getEnd() {
    return ZonedDateTime.parse(this.end).toInstant().getEpochSecond();
  }
}
