package com.auzmor.calendar.services.impls;

import com.auzmor.calendar.controllers.requests.events.AttendeeRequest;
import com.auzmor.calendar.daos.CalendarDao;
import com.auzmor.calendar.models.entities.Event;
import com.auzmor.calendar.models.entities.metadata.EventType;
import com.auzmor.calendar.models.entities.metadata.ObjectType;
import com.auzmor.calendar.services.ApplicationContextService;
import com.auzmor.calendar.services.CalendarService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

import static com.auzmor.calendar.constants.Constant.DEFAULT_EMAIL;


@Service
public class CalendarServiceImpl implements CalendarService {

  @Autowired
  CalendarDao calendarDao;
  @Autowired
  private ApplicationContextService applicationContextService;

  String nylse_token = "ER0CqAMebIF4y4svQzPX7DRGBhl7mD:";
  String nylse_token1 = "CAlWum2Gw39xpt1Y5BpyO6haH5YGwj:";
  String recruiterName = "Pooja Gautam";
  String username = "pooja@auzmor.com";
  String default_username = "mandeep@kovalent.io";

  @Override
  public Object saveEvent(String eventId, String title, String externalTitle, long start, long end, final Set<String> guestEmails, final Set<AttendeeRequest> attendeeIds, String description,
                         String externalDescription, String location) throws JSONException, IOException {

    String email = applicationContextService.getCurrentUserEmail();
    String uuid = UUID.randomUUID().toString().replace("-", "");
    String candidateUUID = UUID.randomUUID().toString().replace("-", "");
    HttpHeaders headers = new HttpHeaders();
    HttpHeaders httpHeaders = new HttpHeaders();
    RestTemplate restTemplate = new RestTemplate();
    headers.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    String organizerToken = convertTokenToBase64(nylse_token);
    String defaultToken = convertTokenToBase64(nylse_token1);

    headers.add("Authorization", "Basic " + organizerToken);
    httpHeaders.add("Authorization", "Basic " + defaultToken);
    String organizer_calendar_Id = getCalendarId(username, organizerToken, restTemplate);
    String default_calendar_Id = getCalendarId(default_username, defaultToken, restTemplate);

    Set<String> attendeeEmailList = new HashSet<>();
    for(AttendeeRequest attendee:attendeeIds) {
      attendeeEmailList.add(attendee.getEmail());
    }

    Map<String, Object> dummyRecruiter = new HashMap();
    dummyRecruiter.put("email", DEFAULT_EMAIL);
    dummyRecruiter.put("name", recruiterName);
    dummyRecruiter.put("status", "yes");
    JSONObject guestJson = calendardataJson(guestEmails, start, end, default_calendar_Id, externalTitle, externalDescription, location, dummyRecruiter);
    JSONObject interviewersJson = calendardataJson(attendeeEmailList, start, end, organizer_calendar_Id, title, description, location, null);


    HttpEntity<String> request = new HttpEntity<String>(interviewersJson.toString(), headers);
    HttpEntity<String> httpRequest = new HttpEntity<String>(guestJson.toString(), httpHeaders);

    ResponseEntity<String> response = restTemplate.postForEntity( "https://api.nylas.com/events?notify_participants=true", request , String.class );
    ResponseEntity<String> candidateResponse = restTemplate.postForEntity( "https://api.nylas.com/events?notify_participants=true", httpRequest , String.class );

    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(response.getBody());
    String calendarData = String.valueOf(root);

    ObjectMapper candidateMapper = new ObjectMapper();
    JsonNode candidateRoot = mapper.readTree(candidateResponse.getBody());
    String candidateEventData = String.valueOf(candidateRoot);

    Event event = new Event(root.get("id").asText(), organizer_calendar_Id, "abc", calendarData , uuid, ObjectType.EVENT, eventId, EventType.INTERNAL);
    Event candidateEvent = new Event(candidateRoot.get("id").asText(), default_calendar_Id, "abc", candidateEventData , candidateUUID, ObjectType.EVENT, eventId, EventType.EXTERNAL);
    calendarDao.saveEvent(event,candidateEvent);
    Map<String, Object> result = new HashMap();
    result.put("response", "ok");
    return result;
  }

  String convertTokenToBase64(String token) {
    byte[] plainCredsBytes = token.getBytes();
    byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
    String base64Creds = new String(base64CredsBytes);
    return base64Creds;
  }

  JSONObject calendardataJson(Set<String> participants, long start, long end, String calendar_Id, String title, String description, String location,
                              Map<String, Object> dummyRecruiter)
  {
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
    timeObject.put("start_time", Long.toString(start));
    timeObject.put("end_time", Long.toString(end));
    m.put("calendar_id", calendar_Id);
    m.put("title", title);
    m.put("when",timeObject);
    m.put("participants",participantsList);
    m.put("description",description);
    m.put("location", location);
    JSONObject json = new JSONObject(m);
    return json;
  }

  String getCalendarId(String username, String base64Creds, RestTemplate restTemplate) throws IOException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Authorization", "Basic " + base64Creds);
    HttpEntity<String> request = new HttpEntity<String>(headers);
    ResponseEntity<String> response = restTemplate.exchange("https://api.nylas.com/calendars", HttpMethod.GET, request, String.class);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(response.getBody());
    String calendar_Id=null;
    if(response.getStatusCodeValue() != 200) {
      return calendar_Id;
    }
    for (Iterator<JsonNode> it = root.elements(); it.hasNext(); ) {
      JsonNode jsonNode = it.next();
      if(jsonNode.get("name").asText().equals(username)) {
        calendar_Id=jsonNode.get("id").asText();
      }
    }
    return calendar_Id;
  }

  public Object updateEvent(String eventId, String title, String externalTitle, long start, long end, final Set<String> guestEmails, final Set<AttendeeRequest> attendeeIds, String description,
                           String externalDescription, String location) throws JSONException, IOException {
    String updateUrl = "https://api.nylas.com/events/{id}?notify_participants=true";
    Map<String, String> calendarIdsMap = calendarDao.mapEvent(eventId);
    String externalEventUrl = updateUrl.replace("{id}",calendarIdsMap.get("EXTERNAL"));
    String internalEventUrl = updateUrl.replace("{id}",calendarIdsMap.get("INTERNAL"));

    HttpHeaders headers = new HttpHeaders();
    HttpHeaders httpHeaders = new HttpHeaders();
    RestTemplate restTemplate = new RestTemplate();
    headers.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    String organizerToken = convertTokenToBase64(nylse_token);
    String defaultToken = convertTokenToBase64(nylse_token1);

    headers.add("Authorization", "Basic " + organizerToken);
    httpHeaders.add("Authorization", "Basic " + defaultToken);
    String organizer_calendar_Id = getCalendarId(username, organizerToken, restTemplate);
    String default_calendar_Id = getCalendarId(default_username, defaultToken, restTemplate);

    Set<String> attendeeEmailList = new HashSet<>();
    for(AttendeeRequest attendee:attendeeIds) {
      attendeeEmailList.add(attendee.getEmail());
    }

    Map<String, Object> dummyRecruiter = new HashMap();
    dummyRecruiter.put("email", DEFAULT_EMAIL);
    dummyRecruiter.put("name", recruiterName);
    dummyRecruiter.put("status", "yes");
    JSONObject guestJson = calendardataJson(guestEmails, start, end, default_calendar_Id, externalTitle, externalDescription, location, dummyRecruiter);
    JSONObject interviewersJson = calendardataJson(attendeeEmailList, start, end, organizer_calendar_Id, title, description, location, null);


    HttpEntity<String> request = new HttpEntity<String>(interviewersJson.toString(), headers);
    HttpEntity<String> httpRequest = new HttpEntity<String>(guestJson.toString(), httpHeaders);

    ResponseEntity<String> externalResponse = restTemplate.exchange(externalEventUrl, HttpMethod.PUT, httpRequest , String.class );
    ResponseEntity<String> internalResponse = restTemplate.exchange(internalEventUrl, HttpMethod.PUT, request , String.class );

    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(externalResponse.getBody());
    String internalEventData = String.valueOf(root);

    ObjectMapper candidateMapper = new ObjectMapper();
    JsonNode candidateRoot = mapper.readTree(internalResponse.getBody());
    String externalEventData = String.valueOf(candidateRoot);

    calendarDao.updateEvent(eventId, internalEventData, externalEventData);
    Map<String, Object> result = new HashMap();
    result.put("response", "ok");
    return result;
  }

  @Override
  public Object checkAvailability(String email, long start, long end) throws IOException {
    HttpHeaders headers = new HttpHeaders();
    Set<String> emails = new HashSet<>();
    emails.add(email);
    RestTemplate restTemplate = new RestTemplate();
    headers.setContentType(MediaType.APPLICATION_JSON);
    byte[] plainCredsBytes = nylse_token.getBytes();
    byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
    String base64Creds = new String(base64CredsBytes);
    headers.add("Authorization", "Basic " + base64Creds);
    Map<String, Object> m = new HashMap();
    JSONObject personJsonObject = new JSONObject();
    m.put("start_time", Long.toString(start));
    m.put("end_time", Long.toString(end));
    m.put("emails", emails);
    JSONObject json = new JSONObject(m);
    HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
    ResponseEntity<String> response = restTemplate.postForEntity("https://api.nylas.com/calendars/free-busy", request, String.class);
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
  public void deleteEvent(String id) {
    String url = "https://api.nylas.com/events/{id}?notify_participants=true";
    Map<String, String> calendarIdsMap = calendarDao.mapEvent(id);
    String externalEventUrl = url.replace("{id}",calendarIdsMap.get("EXTERNAL"));
    String internalEventUrl = url.replace("{id}",calendarIdsMap.get("INTERNAL"));
    HttpHeaders headers = new HttpHeaders();
    HttpHeaders httpHeaders = new HttpHeaders();

    RestTemplate restTemplate = new RestTemplate();
    headers.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    String organizerToken = convertTokenToBase64(nylse_token);
    String defaultToken = convertTokenToBase64(nylse_token1);
    headers.add("Authorization", "Basic " + organizerToken);
    httpHeaders.add("Authorization", "Basic " + defaultToken);
    HttpEntity<String> request = new HttpEntity<String>(null,headers);
    HttpEntity<String> externalRequest = new HttpEntity<String>(null,headers);

    ResponseEntity response = restTemplate.exchange(internalEventUrl, HttpMethod.DELETE, request, String.class);
    ResponseEntity externalResponse = restTemplate.exchange(internalEventUrl, HttpMethod.DELETE, externalRequest, String.class);
    calendarDao.deleteEvent(id);
  }

}
