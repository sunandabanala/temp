package com.auzmor.calendar.services.impls;

import com.auzmor.calendar.controllers.requests.events.AttendeeRequest;
import com.auzmor.calendar.daos.CalendarDao;
import com.auzmor.calendar.exceptions.DBException;
import com.auzmor.calendar.exceptions.errors.Error;
import com.auzmor.calendar.exceptions.errors.ErrorType;
import com.auzmor.calendar.models.entities.Event;
import com.auzmor.calendar.models.entities.metadata.ObjectType;
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

import static com.auzmor.calendar.exceptions.errors.ErrorCode.E0000000;

@Service
public class CalendarServiceImpl implements CalendarService {

  @Autowired
  CalendarDao calendarDao;
  String nylse_token = "ER0CqAMebIF4y4svQzPX7DRGBhl7mD:";

  @Override
  public Event saveEvent(String title, String username, long start, long end, final Set<AttendeeRequest> attendeeIds, String description, String location, String type, String url) throws JSONException, IOException {
    Set<String> emails = new HashSet<>();
    emails.add(username);
    Set<String> a = new HashSet<>();
    String uuid = UUID.randomUUID().toString().replace("-", "");
    HttpHeaders headers = new HttpHeaders();
    RestTemplate restTemplate = new RestTemplate();
    headers.setContentType(MediaType.APPLICATION_JSON);
    byte[] plainCredsBytes = nylse_token.getBytes();
    byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
    String base64Creds = new String(base64CredsBytes);
    headers.add("Authorization", "Basic " + base64Creds);
    //check slots
    List<Error> errors = new ArrayList<>();
    String calendar_Id = getCalendarId(username, base64Creds, restTemplate);

    Map<String, Object> m = new HashMap();
    Map<String, Object> timeObject = new HashMap();
    Set<Map<String, Object>> participantsList = new HashSet<>();
    for(AttendeeRequest email:attendeeIds) {
      Map<String, Object> participants = new HashMap<>();
      participants.put("email", email.getEmail());
      participantsList.add(participants);
    }
    timeObject.put("start_time", Long.toString(start));
    timeObject.put("end_time", Long.toString(end));
    JSONObject personJsonObject = new JSONObject();
    m.put("calendar_id", calendar_Id);
    m.put("title", title);
    m.put("when",timeObject);
    m.put("participants",participantsList);
    m.put("description",description);
    m.put("location", location);
    JSONObject json = new JSONObject(m);
    HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
    ResponseEntity<String> response = restTemplate.postForEntity( "https://api.nylas.com/events?notify_participants=true", request , String.class );
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(response.getBody());
    String calendarData = String.valueOf(root);
    Event event = new Event(root.get("id").asText(), calendar_Id, "abc", calendarData , uuid, ObjectType.EVENT);
    calendarDao.saveEvent(event);
    return null;
  }

  String getCalendarId(String username, String base64Creds, RestTemplate restTemplate) throws IOException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Authorization", "Basic " + base64Creds);
    HttpEntity<String> request = new HttpEntity<String>(headers);
    ResponseEntity<String> response = restTemplate.exchange("https://api.nylas.com/calendars", HttpMethod.GET, request, String.class);
    System.out.println(response.getBody());
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(response.getBody());
    String calendar_Id=null;
    System.out.println(response.getStatusCodeValue() == 200);
    if(response.getStatusCodeValue() != 200) {
      return calendar_Id;
    }
    for (Iterator<JsonNode> it = root.elements(); it.hasNext(); ) {
      JsonNode jsonNode = it.next();
      System.out.println(jsonNode.get("name").asText());
      System.out.println(username);
      if(jsonNode.get("name").asText().equals(username)) {
        calendar_Id=jsonNode.get("id").asText();
      }
    }
    System.out.println("calendar_Id"+calendar_Id);
    return calendar_Id;
  }

  public Event updateEvent(String id, String title, String username, long start, long end, final Set<AttendeeRequest> attendeeIds, String description, String location, String type, String url) throws JSONException, IOException {
    String updateUrl = "https://api.nylas.com/events/{id}";
    String newUpdateUrl  = updateUrl.replace("{id}", id);
    System.out.println(updateUrl);
    Set<String> emails = new HashSet<>();
    emails.add(username);
    Set<String> a = new HashSet<>();
    String uuid = UUID.randomUUID().toString().replace("-", "");
    HttpHeaders headers = new HttpHeaders();
    RestTemplate restTemplate = new RestTemplate();
    headers.setContentType(MediaType.APPLICATION_JSON);
    byte[] plainCredsBytes = nylse_token.getBytes();
    byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
    String base64Creds = new String(base64CredsBytes);
    headers.add("Authorization", "Basic " + base64Creds);
    //check slots
    String calendar_Id = getCalendarId(username, base64Creds, restTemplate);

    Map<String, Object> m = new HashMap();
    Map<String, Object> timeObject = new HashMap();
    Set<Map<String, Object>> participantsList = new HashSet<>();
    System.out.println(attendeeIds);
    for(AttendeeRequest email:attendeeIds) {
      Map<String, Object> participants = new HashMap<>();
      participants.put("email", email.getEmail());
      participantsList.add(participants);
    }
    timeObject.put("start_time", Long.toString(start));
    timeObject.put("end_time", Long.toString(end));
    JSONObject personJsonObject = new JSONObject();
    m.put("calendar_id", calendar_Id);
    m.put("title", title);
    m.put("when",timeObject);
    m.put("participants",participantsList);
    m.put("description",description);
    m.put("location", location);
    System.out.println("m"+m);
    JSONObject json = new JSONObject(m);
    System.out.println("json..."+json);
    HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
    ResponseEntity<String> response = restTemplate.exchange( newUpdateUrl, HttpMethod.PUT, request , String.class );
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(response.getBody());
    System.out.println("new..."+root.asText());
    String calendarData = String.valueOf(root);
    //check calendar
    // ResponseEntity<String> response1 = restTemplate.exchange("https://api.nylas.com/calendars?notify_participants=true", HttpMethod.GET, request, String.class);
    Event e1 = new Event(root.get("id").asText(), calendar_Id, "abc", calendarData , uuid, ObjectType.EVENT);
    System.out.println(e1.getUuid());
    calendarDao.updateEvent(id, calendarData);
    return null;
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
    String url = "https://api.nylas.com/events/{id}";
    String newUpdateUrl  = url.replace("{id}", id);
    HttpHeaders headers = new HttpHeaders();
    RestTemplate restTemplate = new RestTemplate();
    headers.setContentType(MediaType.APPLICATION_JSON);
    byte[] plainCredsBytes = nylse_token.getBytes();
    byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
    String base64Creds = new String(base64CredsBytes);
    headers.add("Authorization", "Basic " + base64Creds);
    restTemplate.delete(url, headers);
  }

}
