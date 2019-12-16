package com.auzmor.calendar.services.impls;

import com.auzmor.calendar.controllers.requests.events.AttendeeRequest;
import com.auzmor.calendar.daos.CalendarDao;
import com.auzmor.calendar.helpers.CalendarEvent;
import com.auzmor.calendar.models.entities.Event;
import com.auzmor.calendar.models.entities.metadata.EventType;
import com.auzmor.calendar.models.entities.metadata.ObjectType;
import com.auzmor.calendar.services.ApplicationContextService;
import com.auzmor.calendar.services.CalendarService;
import com.auzmor.calendar.utils.RestTemplateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sun.research.ws.wadl.HTTPMethods;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.auzmor.calendar.constants.DataConstants.DUMMY_EMAIL;
import static com.auzmor.calendar.constants.NylasApiConstants.*;

@Service
public class CalendarServiceImpl implements CalendarService {

  @Autowired
  CalendarDao calendarDao;
  @Autowired
  private ApplicationContextService applicationContextService;


  @Override
  public Object saveEvent(String eventId, String title, String externalTitle, String start, String end, final Set<String> guestEmails, final Set<AttendeeRequest> attendeeIds, String description,
                         String externalDescription, String location) throws JSONException, IOException {

    String defaultCalendarId;
    String organizerCalendarId;
    String timezone = getTimeZone(start);
    String userId = applicationContextService.getCurrentUserId();
    String accountId = applicationContextService.getAccountId();
    String defaultUserId = applicationContextService.getDefaultUserId();
    String defaultAccountId = applicationContextService.getDefaultAccountId();
    String recruiterName = applicationContextService.getCurrentUsername();
    String uuid = UUID.randomUUID().toString().replace("-", "");
    String candidateUUID = UUID.randomUUID().toString().replace("-", "");

    String organizerToken = applicationContextService.geToken();
    String defaultToken = applicationContextService.getDefaultToken();


    if(defaultToken.equals(organizerToken)) {
      organizerCalendarId = getCalendarId(organizerToken);
      defaultCalendarId = organizerCalendarId;
    }else {
      organizerCalendarId = getCalendarId(organizerToken);
      defaultCalendarId = getCalendarId(defaultToken);
    }

    Set<String> attendeeEmailList = new HashSet<>();
    for(AttendeeRequest attendee:attendeeIds) {
      attendeeEmailList.add(attendee.getEmail());
    }

    Map<String, Object> dummyRecruiter = new HashMap();
    dummyRecruiter.put("email", DUMMY_EMAIL);
    dummyRecruiter.put("name", recruiterName);
    dummyRecruiter.put("status", "yes");
    JSONObject guestJson = calendardataJson(guestEmails, start, end, defaultCalendarId, externalTitle, externalDescription, location, dummyRecruiter);
    JSONObject interviewersJson = calendardataJson(attendeeEmailList, start, end, organizerCalendarId, title, description, location, null);

    ResponseEntity<String> response = RestTemplateUtil.restTemplateUtil(organizerToken, interviewersJson.toString(), CREATE_EVENT, HttpMethod.POST);
    ResponseEntity<String> candidateResponse = RestTemplateUtil.restTemplateUtil(defaultToken, guestJson.toString(), CREATE_EVENT, HttpMethod.POST);

    Gson gson = new Gson();
    CalendarEvent calendarData = gson.fromJson(response.getBody(), CalendarEvent.class);
    CalendarEvent candidateEventData = gson.fromJson(candidateResponse.getBody(), CalendarEvent.class);

    if(defaultToken.equals(organizerToken)) {
      String defaultCursorId = getCursorId(defaultToken);
      calendarDao.updateCursorId(null, defaultCursorId, defaultUserId, null);
    }else {
      String organizerCursorId= getCursorId(organizerToken);
      String defaultCursorId = getCursorId(defaultToken);
      calendarDao.updateCursorId(organizerCursorId, defaultCursorId, defaultUserId, userId);
    }

    Event event = new Event(calendarData.getId(), organizerCalendarId, accountId, calendarData.toString() , uuid, ObjectType.EVENT, eventId, EventType.INTERNAL, timezone);
    Event candidateEvent = new Event(candidateEventData.getId(), defaultCalendarId, defaultAccountId, candidateEventData.toString() , candidateUUID, ObjectType.EVENT, eventId, EventType.EXTERNAL, timezone);
    calendarDao.saveEvent(event,candidateEvent);
    Map<String, Object> result = new HashMap();
    result.put("response", "ok");
    return result;
  }

  String getTimeZone(String start) {
    ZonedDateTime zonedDateTime = ZonedDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME);
    String timezone = TimeZone.getTimeZone(zonedDateTime.getZone().getId()).getID();
    return timezone;
  }

  String getCursorId(String token) throws IOException {
    ResponseEntity<String> response = RestTemplateUtil.restTemplateUtil(token, null, FETCH_LATEST_CURSOR, HttpMethod.POST);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(response.getBody());
    String cursorId = null;
    if(response.getStatusCodeValue() != 200) {
      return cursorId;
    }
    return root.get("cursor").asText();
  }

  JSONObject calendardataJson(Set<String> participants, String start, String end, String calendar_Id, String title, String description, String location,
                              Map<String, Object> dummyRecruiter) {

    long startTime = ZonedDateTime.parse(start).toInstant().getEpochSecond();
    long endTime = ZonedDateTime.parse(end).toInstant().getEpochSecond();
    Map<String, Object> m = new HashMap();
    Map<String, Object> timeObject = new HashMap();
    Set<Map<String, Object>> participantsList = new HashSet<>();
    for(String email:participants) {
      Map<String, Object> participant = new HashMap<>();
      participant.put("email", email);
      participantsList.add(participant);
    }
    if(dummyRecruiter != null) {
      participantsList.add(dummyRecruiter);
    }
    timeObject.put("start_time", startTime);
    timeObject.put("end_time", endTime);
    m.put("calendar_id", calendar_Id);
    m.put("title", title);
    m.put("when",timeObject);
    m.put("participants",participantsList);
    m.put("description",description);
    m.put("location", location);
    JSONObject json = new JSONObject(m);
    return json;
  }

  String getCalendarId(String token) throws IOException {

    ResponseEntity<String> response = RestTemplateUtil.restTemplateUtil(token, null, FETCH_CALENDAR_ID, HttpMethod.GET);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(response.getBody());
    String calendar_Id=null;
    if(response.getStatusCodeValue() != 200) {
      return calendar_Id;
    }
    for (Iterator<JsonNode> it = root.elements(); it.hasNext(); ) {
      JsonNode jsonNode = it.next();
      if(jsonNode.get("object").asText().equals("calendar") && jsonNode.get("read_only").asText().equals("false")) {
        calendar_Id=jsonNode.get("id").asText();
        break;
      }
    }
    return calendar_Id;
  }

  public Object updateEvent(String eventId, String title, String externalTitle, String start, String end, final Set<String> guestEmails, final Set<AttendeeRequest> attendeeIds, String description,
                           String externalDescription, String location) throws JSONException, IOException {
    String default_calendar_Id;
    String organizer_calendar_Id;
    String userId = applicationContextService.getCurrentUserId();
    String defaultUserId = applicationContextService.getDefaultUserId();
    String recruiterName = applicationContextService.getCurrentUsername();
    String organizerToken = applicationContextService.geToken();
    String defaultToken = applicationContextService.getDefaultToken();
    Map<String, String> calendarIdsMap = calendarDao.mapEvent(eventId);
    String externalEventUrl = UPDATE_EVENT.replace("{id}",calendarIdsMap.get("EXTERNAL"));
    String internalEventUrl = UPDATE_EVENT.replace("{id}",calendarIdsMap.get("INTERNAL"));


    if(defaultToken.equals(organizerToken)) {
      organizer_calendar_Id = getCalendarId(organizerToken);
      default_calendar_Id = organizer_calendar_Id;
    }else {
      organizer_calendar_Id = getCalendarId(organizerToken);
      default_calendar_Id = getCalendarId(defaultToken);
    }

    Set<String> attendeeEmailList = new HashSet<>();
    for(AttendeeRequest attendee:attendeeIds) {
      attendeeEmailList.add(attendee.getEmail());
    }

    Map<String, Object> dummyRecruiter = new HashMap();
    dummyRecruiter.put("email", DUMMY_EMAIL);
    dummyRecruiter.put("name", recruiterName);
    dummyRecruiter.put("status", "yes");
    JSONObject guestJson = calendardataJson(guestEmails, start, end, default_calendar_Id, externalTitle, externalDescription, location, dummyRecruiter);
    JSONObject interviewersJson = calendardataJson(attendeeEmailList, start, end, organizer_calendar_Id, title, description, location, null);

    ResponseEntity<String> internalResponse = RestTemplateUtil.restTemplateUtil(organizerToken, interviewersJson.toString(), internalEventUrl, HttpMethod.PUT);
    ResponseEntity<String> externalResponse = RestTemplateUtil.restTemplateUtil(defaultToken, guestJson.toString(), externalEventUrl, HttpMethod.PUT);

    Gson gson = new Gson();
    CalendarEvent internalEventData = gson.fromJson(internalResponse.getBody(), CalendarEvent.class);
    CalendarEvent externalEventData = gson.fromJson(externalResponse.getBody(), CalendarEvent.class);


    if(defaultToken.equals(organizerToken)) {
      String defaultCursorId = getCursorId(defaultToken);
      calendarDao.updateCursorId(null, defaultCursorId, defaultUserId, null);
    }else {
      String organizerCursorId= getCursorId(organizerToken);
      String defaultCursorId = getCursorId(defaultToken);
      calendarDao.updateCursorId(organizerCursorId, defaultCursorId, defaultUserId, userId);
    }

    calendarDao.updateEvent(eventId, internalEventData.toString(), externalEventData.toString());
    Map<String, Object> result = new HashMap();
    result.put("response", "ok");
    return result;
  }

  @Override
  public Object checkAvailability(String email, long start, long end) throws IOException {
    Set<String> emails = new HashSet<>();
    emails.add(email);
    String organizerToken = applicationContextService.geToken();
    Map<String, Object> m = new HashMap();
    m.put("start_time", Long.toString(start));
    m.put("end_time", Long.toString(end));
    m.put("emails", emails);
    JSONObject json = new JSONObject(m);
    ResponseEntity<String> response = RestTemplateUtil.restTemplateUtil(organizerToken, json.toString(), CHECK_AVAILABILITY, HttpMethod.POST);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(response.getBody());
    Map<String, Object> result = new HashMap();
    if (response.getStatusCodeValue() == 200 && root.get(0).get("time_slots").size() == 0) {
      result.put("Availability", true);
      return result;
    }
    result.put("Availability", false);
    return result;
  }

  @Override
  public void deleteEvent(String id) throws IOException {
    String userId = applicationContextService.getCurrentUserId();
    Map<String, String> calendarIdsMap = calendarDao.mapEvent(id);
    String defaultUserId = applicationContextService.getDefaultUserId();
    String externalEventUrl = DELETE_EVENT.replace("{id}",calendarIdsMap.get("EXTERNAL"));
    String internalEventUrl = DELETE_EVENT.replace("{id}",calendarIdsMap.get("INTERNAL"));

    String organizerToken = applicationContextService.geToken();
    String defaultToken = applicationContextService.getDefaultToken();

    ResponseEntity<String> internalResponse = RestTemplateUtil.restTemplateUtil(organizerToken, null, internalEventUrl, HttpMethod.DELETE);
    ResponseEntity<String> externalResponse = RestTemplateUtil.restTemplateUtil(defaultToken, null, externalEventUrl, HttpMethod.DELETE);

    if(defaultToken.equals(organizerToken)) {
      String defaultCursorId = getCursorId(defaultToken);
      calendarDao.updateCursorId(null, defaultCursorId, defaultUserId, null);
    }else {
      String organizerCursorId= getCursorId(organizerToken);
      String defaultCursorId = getCursorId(defaultToken);
      calendarDao.updateCursorId(organizerCursorId, defaultCursorId, defaultUserId, userId);
    }
    calendarDao.deleteEvent(id);
  }

}
