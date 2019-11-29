package com.auzmor.calendar.mappers;

import com.auzmor.calendar.models.Event;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CalendarMapper {

  void saveEvent(@Param("event") final Event event);
}
