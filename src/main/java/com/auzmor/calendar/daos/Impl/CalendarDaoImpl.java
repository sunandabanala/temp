package com.auzmor.calendar.daos.Impl;

import com.auzmor.calendar.daos.CalendarDao;
import com.auzmor.calendar.mappers.CalendarMapper;
import com.auzmor.calendar.models.entities.Event;
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

  @Override
  public Event updateEvent(String id, String calendarData) {
    calendarMapper.updateEvent(id, calendarData);
    return null;
  }
}
