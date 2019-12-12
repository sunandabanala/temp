package com.auzmor.calendar.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEvent {

  private String owner;
  private String calendar_id;
  private String description;
  private String message_id;
  private String title;
  private CalendarEventTime when;
  private String account_id;
  private Boolean read_only;
  private String location;
  private Boolean busy;
  private String object;
  private String status;
  private String id;
  private List<Participant> participants;

}
