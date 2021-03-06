package com.auzmor.calendar.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GoogleCreateEventRequestBody {
  private String kind;
  private String summary;
  private String description;
  private String location;
  private Organizer organizer;
  private Organizer creator;
  private DateObj start;
  private DateObj end;
  private String eventType;
  private ConferenceData conferenceData;
  private List<Organizer> attendees;
  private Map reminders;
  private String hangoutLink;
  private String iCalUID;
  private String created;
  private String updated;
  private String htmlLink;
  private String status;
  private String etag;
  private String id;

}
