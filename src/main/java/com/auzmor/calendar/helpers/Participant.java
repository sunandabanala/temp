package com.auzmor.calendar.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Participant {
  private String name;
  private String comment;
  private String email;
  private String status;
}
