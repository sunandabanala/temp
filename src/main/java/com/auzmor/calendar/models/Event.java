package com.auzmor.calendar.models.entities;

import com.auzmor.calendar.models.UserAccount;
import com.auzmor.calendar.models.entities.metadata.EventType;
import com.auzmor.calendar.models.entities.metadata.ObjectType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springfox.documentation.spring.web.json.Json;

@Getter
@Setter
@NoArgsConstructor
public class Event extends com.auzmor.calendar.models.entities.Entity {

  private Integer id;
  private String objectId;
  private String calendarId;
  private String accountId;
  private String calendarDetails;
  private ObjectType objectType;
  private String eventId;
  private EventType eventType;
  private UserAccount account;
  private String timezone;

  public Event(String objectId, String calendarId, String accountId, String calendarDetails, String uuid, ObjectType objectType, String eventId
               , EventType eventType, String timezone) {
    super(uuid);
    this.objectId=objectId;
    this.calendarId=calendarId;
    this.accountId=accountId;
    this.calendarDetails=calendarDetails;
    this.objectType = objectType;
    this.eventId = eventId;
    this.eventType=eventType;
    this.timezone=timezone;
  }

}
