package com.auzmor.calendar.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springfox.documentation.spring.web.json.Json;

@Getter
@Setter
@NoArgsConstructor
public class Event extends Entity{

  private Integer id;
  private String eventId;
  private String calendarId;
  private String accountId;
  private Json calendarDetails;

  public Event(String eventId, String calendarId, String accountId, Json calendarDetails, String uuid) {
    super(uuid);
    this.eventId=eventId;
    this.calendarId=calendarId;
    this.accountId=accountId;
    this.calendarDetails=calendarDetails;
  }

}
