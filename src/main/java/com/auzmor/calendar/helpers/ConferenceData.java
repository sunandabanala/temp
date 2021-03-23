package com.auzmor.calendar.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConferenceData {
  private CreateRequest createRequest;
  private String conferenceId;
  private List<EntryPoint> entryPoints;
}
