package com.auzmor.calendar.daos.Impl;

import com.auzmor.calendar.constants.NylasApiConstants;
import com.auzmor.calendar.daos.AccountDao;
import com.auzmor.calendar.daos.CalendarDao;
import com.auzmor.calendar.daos.WebhookDao;
import com.auzmor.calendar.helpers.CalendarEvent;
import com.auzmor.calendar.helpers.CursorDiff;
import com.auzmor.calendar.helpers.Delta;
import com.auzmor.calendar.mappers.CalendarMapper;
import com.auzmor.calendar.models.entities.Event;
import com.auzmor.calendar.models.entities.metadata.EventType;
import com.auzmor.calendar.utils.RestTemplateUtil;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WebhookDaoImpl implements WebhookDao {

  private static final Logger logger = LoggerFactory.getLogger(WebhookDaoImpl.class);

  @Autowired
  private AccountDao accountDao;

  @Autowired
  private CalendarMapper calendarMapper;

  @Autowired
  private CalendarDao calendarDao;

  @Override
  public void handleWebhook(String cursorId, String token, String accountId) throws Exception {
    ResponseEntity<?> response = RestTemplateUtil.restTemplateUtil(token, null, NylasApiConstants.FETCH_DELTAS+cursorId+"&include_types=event", HttpMethod.GET, CursorDiff.class);
    CursorDiff cursorDiff = (CursorDiff) response.getBody();
    if (!cursorId.equals(cursorDiff.getCursor_end())) {
      Map<String, CalendarEvent> events = processDeltas(cursorDiff.getDeltas());
      Set<String> calendarEventIds = events.keySet();
      calendarEventIds.removeIf(id -> (id == null));
      try {
        if (!calendarEventIds.isEmpty()) {
          List<Event> eventList = calendarMapper.getEventsWithTokens(calendarEventIds);
          Map<String, List<String>> eventObjectMap = new HashMap<>();
          Map<String, Event> objectDetailsMap = new HashMap();
          List<Map> updateEvents = new ArrayList<>();
          List nylasApis = new ArrayList();
          List<Map> platformUpdateEvents = new ArrayList<>();
          if (eventList != null && !eventList.isEmpty()) {
            for (int i = 0; i < eventList.size(); i++) {
              objectDetailsMap.put(eventList.get(i).getObjectId(), eventList.get(i));
              List<String> ids = new ArrayList<>();
              if (eventObjectMap.containsKey(eventList.get(i).getEventId())) {
                ids = eventObjectMap.get(eventList.get(i).getEventId());
              }
              ids.add(eventList.get(i).getObjectId());
              eventObjectMap.put(eventList.get(i).getEventId(), ids);
            }

            for (String calendarEventId : calendarEventIds) {
              if (objectDetailsMap.containsKey(calendarEventId) && objectDetailsMap.get(calendarEventId).getEventType() == EventType.INTERNAL) {
                Gson gson = new Gson();
                CalendarEvent c = gson.fromJson(objectDetailsMap.get(calendarEventId).getCalendarDetails(), CalendarEvent.class);
                CalendarEvent e2 = events.get(calendarEventId);
                int isDeleted = e2.getStatus().equals("cancelled") ? 1 : 0;
                updateEvents = getEventsToUpdate(calendarEventId, gson.toJson(e2), updateEvents, isDeleted);
                platformUpdateEvents = getPlatformEventsToUpdate(objectDetailsMap.get(calendarEventId).getEventId(), e2, objectDetailsMap.get(calendarEventId).getTimeZone(), platformUpdateEvents);
                Boolean locationUpdated = (e2.getLocation() != null && c.getLocation() == null) || (e2.getLocation() != null && c.getLocation() != null && !(c.getLocation().equals(e2.getLocation())));
                if (c != null && c.getWhen().getEnd_time() != e2.getWhen().getEnd_time() || c.getWhen().getStart_time() != e2.getWhen().getStart_time() || locationUpdated || e2.getStatus().equals("cancelled")) {
                  String secondObjectId = eventObjectMap.get(objectDetailsMap.get(calendarEventId).getEventId()).get(0).equals(calendarEventId) ? eventObjectMap.get(objectDetailsMap.get(calendarEventId).getEventId()).get(1) : eventObjectMap.get(objectDetailsMap.get(calendarEventId).getEventId()).get(0);
                  CalendarEvent c2 = gson.fromJson(objectDetailsMap.get(secondObjectId).getCalendarDetails(), CalendarEvent.class);
                  c2.getWhen().setEnd_time(e2.getWhen().getEnd_time());
                  c2.getWhen().setStart_time(e2.getWhen().getStart_time());
                  c2.setStatus(e2.getStatus());
                  String eventStr = gson.toJson(c2, CalendarEvent.class);
                  updateEvents = getEventsToUpdate(secondObjectId, eventStr, updateEvents, isDeleted);
                  nylasApis = getNylasApiMap(secondObjectId, c2, objectDetailsMap.get(secondObjectId).getAccount().getNylasToken(), objectDetailsMap.get(secondObjectId).getCalendarId(), nylasApis);
                }
              }
            }
            updateDB(updateEvents, nylasApis, platformUpdateEvents, accountId, cursorDiff.getCursor_end());
          }
        }
      } catch(Exception exception) {
        System.out.println(exception.getStackTrace());
        logger.error("Unable to read events from webhook: " +exception.getMessage());
        return;
      }
    }
  }

  private Map<String, CalendarEvent> processDeltas(List<Delta> deltas) throws Exception {
    Map<String, CalendarEvent> events = new HashMap<>();
    for (int i=0; i<deltas.size(); i++) {
      Delta delta = deltas.get(i);
      if (delta.getEvent().equals("modify") && delta.getObject().equals("event")) {
        events.put(delta.getId(), delta.getAttributes());
      }
    }
    return events;
  }

  private List<Map> getEventsToUpdate(String id, String calendarDetails, List<Map> currentEventsToUpdate, int isDeleted) {
    List<Map> newEventsToUpdate = currentEventsToUpdate;
    Map event = new HashMap();
    event.put("id", id);
    event.put("calendarDetails", calendarDetails);
    event.put("isDeleted", isDeleted);
    newEventsToUpdate.add(event);
    return newEventsToUpdate;
  }

  private List<Map> getPlatformEventsToUpdate(String eventId, CalendarEvent calendarEvent, String timeZone, List<Map> currentPEToUpdate) {
    List<Map> newPEToUpdate = currentPEToUpdate;
    Map event = new HashMap();
    event.put("id", eventId);
    event.put("event", calendarEvent);
    event.put("timeZone", timeZone);
    newPEToUpdate.add(event);
    return newPEToUpdate;
  }

  private List<Map> getNylasApiMap(String id, CalendarEvent c, String token, String calendarId, List<Map> nylasApis) {
    List<Map> newNylasApis = nylasApis;
    Map nylasApi = new HashMap();
    nylasApi.put("id", id);
    Map<String, Object> time = new HashMap();
    time.put("start_time", c.getWhen().getStart_time());
    time.put("end_time", c.getWhen().getEnd_time());
    Map<String, Object> map = new HashMap<>();
    if (c.getStatus().equals("cancelled")) {
      map.put("status", c.getStatus());
    }
    map.put("when", time);
    nylasApi.put("when", map);
    nylasApi.put("token", token);
    nylasApi.put("calendarId", calendarId);
    newNylasApis.add(nylasApi);
    return newNylasApis;
  }

  private void updateDB(List<Map> updateEvents, List<Map> nylasApis, List<Map> platformUpdateEvents, String accountId, String latestCursor) {
    calendarDao.updateEvents(updateEvents);
    calendarDao.updateNylasApis(nylasApis);
    calendarDao.updatePlatformEvents(platformUpdateEvents);
    accountDao.updateAccount(accountId, latestCursor);
  }

}
