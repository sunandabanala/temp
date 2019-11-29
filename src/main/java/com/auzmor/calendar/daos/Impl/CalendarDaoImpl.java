package com.auzmor.calendar.daos.Impl;

import com.auzmor.calendar.daos.CalendarDao;
import com.auzmor.calendar.mappers.CalendarMapper;
import com.auzmor.calendar.models.Event;
import com.auzmor.calendar.services.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalendarDaoImpl implements CalendarDao {

  @Autowired
  CalendarMapper calendarMapper;

  @Override
  public Event saveEvent(Event event) {
    calendarMapper.saveEvent(event);
    return null;
  }
}
