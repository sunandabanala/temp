package com.auzmor.calendar.models.entities;

import com.auzmor.calendar.models.entities.metadata.ObjectType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springfox.documentation.spring.web.json.Json;

@Getter
@Setter
@NoArgsConstructor
public class Event extends Entity {

  private Integer id;
  private String objectId;
  private String calendarId;
  private String accountId;
  private String calendarDetails;
  private ObjectType objectType;

  public Event(String objectId, String calendarId, String accountId, String calendarDetails, String uuid, ObjectType objectType) {
    super(uuid);
    this.objectId=objectId;
    this.calendarId=calendarId;
    this.accountId=accountId;
    this.calendarDetails=calendarDetails;
    this.objectType = objectType;
  }

}
