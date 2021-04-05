package com.auzmor.calendar.services.impls;

import com.auzmor.calendar.daos.CalendarDao;
import com.auzmor.calendar.helpers.CalendarEvent;
import com.auzmor.calendar.helpers.Conferencing;
import com.auzmor.calendar.mappers.GoogleEventMapper;
import com.auzmor.calendar.models.entities.Event;
import com.auzmor.calendar.models.entities.metadata.EventType;
import com.auzmor.calendar.models.entities.metadata.ObjectType;
import com.auzmor.calendar.services.AsyncService;
import com.auzmor.calendar.utils.RestTemplateUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;
import java.util.UUID;

import static com.auzmor.calendar.constants.NylasApiConstants.GET_EVENT;

public class AsyncServiceImpl implements AsyncService {

  @Autowired
  private GoogleEventMapper googleEventMapper;

  @Autowired
  private CalendarDao calendarDao;

  @Override
  @Async
  public void saveGoogleEvent(String nylasEventId, String token, String platformEventId, String cursorId, String timezone, String userId) {
    String url = GET_EVENT.replace("{id}", nylasEventId);
    ResponseEntity<?> response = RestTemplateUtil.restTemplateUtil(token, null, url, HttpMethod.GET, String.class);
    Gson gson = new Gson();
    String uuid = UUID.randomUUID().toString().replace("-", "");
    CalendarEvent calendarData = (CalendarEvent) response.getBody();
    Event event = new Event(calendarData.getId(), calendarData.getCalendar_id(), calendarData.getAccount_id(), gson.toJson(calendarData) , uuid, ObjectType.EVENT,platformEventId, EventType.INTERNAL, timezone);
    calendarDao.updateCursorId(cursorId, userId);
  }

  /*Conferencing conferencing = event.getConferencing();
    if (conferencing != null && conferencing.getDetails() != null && conferencing.getProvider() != null && conferencing.getProvider().equals("Google Meet") ) {
    String meetlink = String.valueOf(conferencing.getDetails().get("url"));
    String platformEventId = googleEventMapper.getByGmeets(meetlink);

  }*/
}
