package com.auzmor.calendar.models.entities;

import com.auzmor.calendar.models.entities.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleEvent extends Entity {
  private String id;
  private String accountId;
  private String googleEventId;
  private String eventDetails;
  private String meetLink;
  private String userId;
  private String timezone;
  private String platformEventId;
}
