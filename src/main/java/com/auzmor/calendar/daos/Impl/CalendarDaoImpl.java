package com.auzmor.calendar.daos.Impl;

import com.auzmor.calendar.constants.ApiConstants;
import com.auzmor.calendar.constants.DataConstants;
import com.auzmor.calendar.constants.NylasApiConstants;
import com.auzmor.calendar.daos.CalendarDao;
import com.auzmor.calendar.helpers.CalendarEvent;
import com.auzmor.calendar.mappers.CalendarMapper;
import com.auzmor.calendar.models.entities.Event;
import com.auzmor.calendar.utils.RestTemplateUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CalendarDaoImpl implements CalendarDao {

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

  public void updateNylasApis(List<Map> apis) {
    for (int i=0; i<apis.size(); i++) {
      String url = NylasApiConstants.UPDATE_EVENT;
      String updatedUrl = url.replace("{id}", apis.get(i).get("id").toString());
      Gson gson = new Gson();
      String body = gson.toJson(apis.get(i).get("when"));
      ResponseEntity<String> response = RestTemplateUtil.restTemplateUtil(apis.get(i).get("token").toString(), body, updatedUrl, HttpMethod.PUT);
    }
  }

  @Override
  public void updatePlatformEvents(List<Map> events) {
    Gson gson = new Gson();
    for (int i=0; i<events.size(); i++) {
      Map event = new HashMap();
      CalendarEvent calendarEvent = (CalendarEvent) events.get(i).get("event");
      event.put("start", calendarEvent.getWhen().getStart_time());
      event.put("end", calendarEvent.getWhen().getEnd_time());
      event.put("title", calendarEvent.getTitle());
      event.put("description", calendarEvent.getDescription());
      event.put("location", calendarEvent.getLocation());
      event.put("timeZone", events.get(i).get("timeZone"));
      String body = gson.toJson(event);
      String url = System.getenv(DataConstants.PLATFORM_HOST)+ApiConstants.PLATFORM_EVENT_API+"/"+events.get(i).get("id");
      RestTemplateUtil.restTemplateUtil(null, body, url, HttpMethod.PUT);
    }
  }

}
