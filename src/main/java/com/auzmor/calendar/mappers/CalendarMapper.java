package com.auzmor.calendar.mappers;

import com.auzmor.calendar.models.entities.Event;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CalendarMapper {

  void saveEvent(@Param("event") final Event event);
  void updateEvent(@Param("id") final String id, @Param("calendarData") final String calendarData);
}
