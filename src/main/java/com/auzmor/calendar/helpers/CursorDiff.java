package com.auzmor.calendar.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CursorDiff {
  private String cursor_end;
  private String cursor_start;
  private List<Delta> deltas;
}
