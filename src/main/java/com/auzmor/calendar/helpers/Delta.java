package com.auzmor.calendar.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Delta {
  private String cursor;
  private String event;
  private String id;
  private String object;
  private CalendarEvent attributes;
}
