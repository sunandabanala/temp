package com.auzmor.calendar.controllers.requests.events;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@ApiModel(description="Request object to create a new Event.")
public class EventUpdateRequest {

  @ApiModelProperty(notes="Title of event", required = true, example = "StandUp")
  @NotNull(message = "Title cannot be null.")
  private String title;

  @ApiModelProperty(notes="external Title of event", required = true, example = "StandUp")
  @NotNull(message = "externalTitle cannot be null.")
  private String externalTitle;

  @ApiModelProperty(notes="Start time/date of the event.", required = true, example = "2019-12-17T16:58:03.209657+05:30[Asia/Kolkata]")
  @NotNull(message = "start date/time cannot be null.")
  //TODO add custom validators to ZonedDateTime
  private String start;

  @ApiModelProperty(notes="End time/date of the event.", required = true, example = "2019-12-17T16:58:03.209657+05:30[Asia/Kolkata]")
  @NotNull(message = "end date/time cannot be null.")
  private String end;

  @ApiModelProperty(notes="All invitees for this event", example = "[    {\n" +
    "      \"email\" : \"abc@auzmor.com\",\n" +
    "      \"id\": \"afa418e740cb4c2fbe7123e210cf8680\"\n" +
    "    },\n" +
    "    {\n" +
    "      \"email\" : \"abc@gmail.com\""+
    "    }]")
  private Set<EmployeeQueryRequest> inviteeIds;

  @ApiModelProperty(notes="All guests for this event", example = "[\"abcd@auzmor.com\"]")
  private Set<String> guestEmails;

  @ApiModelProperty(notes="Title of event", required = true, example = "StandUp")
  private String description;

  @ApiModelProperty(notes="externalDescription of event", required = true, example = "StandUp")
  private String externalDescription;

  @ApiModelProperty(notes="Title of event", required = true, example = "StandUp")
  private String location;

  @ApiModelProperty(notes="Title of event", required = true, example = "StandUp")
  private String externalLocation;

  private Map conferenceMap;

  private Map extConf;

  private Boolean gmeet;

}
