package com.auzmor.calendar.mappers;

import com.auzmor.calendar.models.entities.Event;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CalendarMapper {

  void saveEvent(@Param("event") final Event event);
  void updateEvent(@Param("id") final String id, @Param("calendarData") final String calendarData);
  void deleteEvent(@Param("id") final String id);
  List<Map<String,String>> getCalendarIds(@Param("id") final String id);
  String getTokenByEmail(@Param("email") final String email);
  String getTokenByUserId(@Param("userId") final String userId);
  void updateCursorIdByEmail(@Param("cursorId") final String cursorId, @Param("email") final String email);
  void updateCursorIdByUserId(@Param("cursorId") final String cursorId, @Param("userId") final String userId);

}
