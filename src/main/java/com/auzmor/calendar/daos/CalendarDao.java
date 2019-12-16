package com.auzmor.calendar.daos;

import com.auzmor.calendar.models.entities.Event;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface CalendarDao {

  void saveEvent(Event event, Event candidateEvent);

  void updateEvent(String id, String internalEventData, String externalEventData);

  void deleteEvent(String id);

  Map mapEvent(String id);

  void updateCursorId(String cursorId, String defaultCursorId, String defaultUserId, String userId);

  void updateEvents(List<Map> events);

  void updateNylasApis(List<Map> apiList);

  void updatePlatformEvents(List<Map> events);

}
