package com.auzmor.calendar.daos.Impl;

import com.auzmor.calendar.constants.NylasApiConstants;
import com.auzmor.calendar.daos.AccountDao;
import com.auzmor.calendar.daos.CalendarDao;
import com.auzmor.calendar.daos.WebhookDao;
import com.auzmor.calendar.helpers.CalendarEvent;
import com.auzmor.calendar.mappers.CalendarMapper;
import com.auzmor.calendar.models.entities.Event;
import com.auzmor.calendar.utils.RestTemplateUtil;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WebhookDaoImpl implements WebhookDao {

  @Autowired
  private AccountDao accountDao;

  @Autowired
  private CalendarMapper calendarMapper;

  @Autowired
  private CalendarDao calendarDao;

  @Override
  public void handleWebhook(String cursorId, String token, String accountId) throws Exception {
    ResponseEntity<String> response = RestTemplateUtil.restTemplateUtil(token, null, NylasApiConstants.FETCH_DELTAS+cursorId, HttpMethod.GET);
    JSONObject jo = new JSONObject(response.getBody());
    String latestCursor = jo.get("cursor_end").toString();
    if (!cursorId.equals(latestCursor)) {
      JSONArray deltas = (JSONArray)jo.get("deltas");
      Map<String, Object> events = processDeltas(deltas);
      Set<String> calendarEventIds = events.keySet();
      List<Event> eventList = calendarMapper.getEventsWithTokens(calendarEventIds);
      Map<String, List<String>> eventObjectMap = new HashMap<>();
      Map<String, Event> objectDetailsMap = new HashMap();
      List<Map> updateEvents = new ArrayList<>();
      List nylasApis = new ArrayList();
      List<Map> platformUpdateEvents = new ArrayList<>();
      for (int i=0; i<eventList.size(); i++) {
        objectDetailsMap.put(eventList.get(i).getObjectId(), eventList.get(i));
        List<String> ids = new ArrayList<>();
        if (eventObjectMap.containsKey(eventList.get(i).getEventId())) {
          ids = eventObjectMap.get(eventList.get(i).getEventId());
        }
        ids.add(eventList.get(i).getObjectId());
        eventObjectMap.put(eventList.get(i).getEventId(), ids);
      }
      for (String calendarEventId: calendarEventIds) {
        if (objectDetailsMap.containsKey(calendarEventId)) {
          Gson gson = new Gson();
          CalendarEvent c = gson.fromJson(objectDetailsMap.get(calendarEventId).getCalendarDetails(), CalendarEvent.class);
          JSONObject obj = (JSONObject) events.get(calendarEventId);
          JSONObject jsonobj = (JSONObject) obj.get("when");
          Integer start = (Integer) jsonobj.get("start_time");
          Integer end = (Integer) jsonobj.get("end_time");
          long startTime = start.longValue();
          long endTime = end.longValue();
          Map firstEvent = new HashMap();
          firstEvent.put("id", calendarEventId);
          firstEvent.put("calendarDetails", obj.toString());
          Map event = new HashMap();
          event.put("id", objectDetailsMap.get(calendarEventId).getEventId());
          event.put("event", c);
          event.put("timeZone", objectDetailsMap.get(calendarEventId).getTimeZone());
          platformUpdateEvents.add(event);
          updateEvents.add(firstEvent);
          if (c.getWhen().getEnd_time() != endTime || c.getWhen().getStart_time() != startTime || c.getLocation() != obj.get("location").toString()) {
            String secondObjectId = eventObjectMap.get(objectDetailsMap.get(calendarEventId).getEventId()).get(0).equals(calendarEventId) ? eventObjectMap.get(objectDetailsMap.get(calendarEventId).getEventId()).get(1) : eventObjectMap.get(objectDetailsMap.get(calendarEventId).getEventId()).get(0) ;
            Map secondEvent = new HashMap();
            CalendarEvent c2 = gson.fromJson(objectDetailsMap.get(secondObjectId).getCalendarDetails(), CalendarEvent.class);
            c2.getWhen().setEnd_time(endTime);
            c2.getWhen().setStart_time(startTime);
            String eventStr = gson.toJson(c2, CalendarEvent.class);
            secondEvent.put("id", secondObjectId);
            secondEvent.put("calendarDetails", eventStr);
            updateEvents.add(secondEvent);
            Map nylasApi = new HashMap();
            nylasApi.put("id", secondObjectId);
            nylasApi.put("when", c2.getWhen());
            nylasApi.put("token", objectDetailsMap.get(secondObjectId).getAccount().getNylasToken());
            nylasApi.put("calendarId", objectDetailsMap.get(secondObjectId).getCalendarId());
            nylasApis.add(nylasApi);
          }
        }
      }
      calendarDao.updateEvents(updateEvents);
      calendarDao.updateNylasApis(nylasApis);
      calendarDao.updatePlatformEvents(platformUpdateEvents);
      accountDao.updateAccount(accountId, latestCursor);
    }
  }

  private Map<String, Object> processDeltas(JSONArray deltas) throws Exception {
    Map<String, Object> events = new HashMap<>();
    for (int i=0; i<deltas.length(); i++) {
      JSONObject jsonObject = deltas.getJSONObject(i);
      if (jsonObject.get("event").toString().equals("modify")) {
        events.put(jsonObject.get("id").toString(), jsonObject.get("attributes"));
      }
    }
    return events;
  }

}
