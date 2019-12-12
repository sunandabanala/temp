package com.auzmor.calendar.daos;

import com.auzmor.calendar.models.entities.Event;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface CalendarDao {

  Event saveEvent(Event event, Event candidateEvent);

  Event updateEvent(String id, String internalEventData, String externalEventData);

  void deleteEvent(String id);

  Map mapEvent(String id);

  void updateCursorId(String cursorId, String defaultCursorId, String email, String userId);

  void updateEvents(List<Map> events);

}
