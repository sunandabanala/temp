package com.auzmor.calendar.daos.Impl;

import com.auzmor.calendar.daos.CalendarDao;
import com.auzmor.calendar.mappers.CalendarMapper;
import com.auzmor.calendar.models.entities.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CalendarDaoImpl implements CalendarDao {

  @Value("${default_email}")
  private String defaultEmail;
  @Autowired
  CalendarMapper calendarMapper;

  @Override
  public Event saveEvent(Event event, Event candidateEvent) {
    calendarMapper.saveEvent(event);
    calendarMapper.saveEvent(candidateEvent);
    return null;
  }

  @Override
  public Event updateEvent(String id, String internalEventData, String externalEventData) {
    calendarMapper.updateEvent(id, internalEventData);
    calendarMapper.updateEvent(id, externalEventData);
    return null;
  }

  @Override
  public void deleteEvent(String id) {
    calendarMapper.deleteEvent(id);
  }

  @Override
  public Map mapEvent(String id) {
    List<Map<String,String>> map = calendarMapper.getCalendarIds(id);
    Map<String, String> calendarIdsMap = new HashMap<>();
    for(int i = 0; i < map.size(); i++) {
      calendarIdsMap.put(map.get(i).get("event_type"), map.get(i).get("object_id"));
    }
    return calendarIdsMap;
  }

  @Override
  public void updateCursorId(String cursorId, String defaultCursorId, String email, String userId) {
    calendarMapper.updateCursorIdByEmail(defaultCursorId, email);
    if (cursorId != null)
      calendarMapper.updateCursorIdByUserId(cursorId, userId);
  }

  public void updateEvents(List<Map> events) {
    calendarMapper.updateEvents(events);
  }

}
