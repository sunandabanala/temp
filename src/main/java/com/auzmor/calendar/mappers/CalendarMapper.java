package com.auzmor.calendar.mappers;

import com.auzmor.calendar.models.entities.Event;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface CalendarMapper {

  void updateListOfEvent(@Param("events") final List events);
  void deleteEvent(@Param("id") final String id);
  List<Map<String,String>> getCalendarIds(@Param("id") final String id);
  Map<String, String> getDefaultTokenDataByEmail(@Param("email") final String email);
  List<Event> getEventsWithTokens(@Param("objectIds") final Set objectIds);
  void updateEvents(@Param("events") final List events);
  void saveEvents(@Param("events") final List events);
  void updateListOfCursorIds(@Param("events") final List events);

}
