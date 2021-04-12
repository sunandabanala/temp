package com.auzmor.calendar.services.impls;

import com.auzmor.calendar.controllers.requests.events.AttendeeRequest;
import com.auzmor.calendar.controllers.requests.events.EmployeeQueryRequest;
import com.auzmor.calendar.daos.CalendarDao;
import com.auzmor.calendar.helpers.*;
import com.auzmor.calendar.mappers.GoogleEventMapper;
import com.auzmor.calendar.models.entities.Event;
import com.auzmor.calendar.models.entities.GoogleEvent;
import com.auzmor.calendar.models.entities.metadata.EventType;
import com.auzmor.calendar.models.entities.metadata.ObjectType;
import com.auzmor.calendar.services.ApplicationContextService;
import com.auzmor.calendar.services.CalendarService;
import com.auzmor.calendar.utils.RestTemplateUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.auzmor.calendar.constants.ApiConstants.*;
import static com.auzmor.calendar.constants.DataConstants.*;
import static com.auzmor.calendar.constants.NylasApiConstants.*;
import static com.auzmor.calendar.utils.RestTemplateUtil.mapToObject;
import static com.auzmor.calendar.utils.RestTemplateUtil.objectToMap;

@Service
public class CalendarServiceImpl implements CalendarService {

  @Autowired
  CalendarDao calendarDao;

  @Autowired
  private ApplicationContextService applicationContextService;

  @Autowired
  private GoogleEventMapper googleEventMapper;

  @Override
  public Object saveEvent(String eventId, String title, String externalTitle, String start, String end, final Set<String> guestEmails, final Set<EmployeeQueryRequest> attendeeIds, String description,
                          String externalDescription, String location, String externalLocation, Boolean gmeet, Map conference, Map extConference) throws Exception {
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
    String providerType = applicationContextService.getProviderType();


    if(defaultToken.equals(organizerToken)) {
      organizerCalendarId = getCalendarId(organizerToken);
      defaultCalendarId = organizerCalendarId;
    }else {
      organizerCalendarId = getCalendarId(organizerToken);
      defaultCalendarId = getCalendarId(defaultToken);
    }

    Set<Map> attendeeEmailList = new HashSet<>();
    if (attendeeIds != null) {
      for (EmployeeQueryRequest attendee : attendeeIds) {
        Map map = new HashMap();
        map.put("email", attendee.getEmail());
        map.put("name", attendee.getFirstName());
        attendeeEmailList.add(map);
      }
    }

    String candidateEmail=null;
    Iterator iterator = guestEmails.iterator();
    while(iterator.hasNext()) {
      candidateEmail = (String) iterator.next();
    }

    Map<String, Object> dummyRecruiter = new HashMap();
    Map<String, Object> dummyCandidate = new HashMap();

    dummyCandidate.put("email", DUMMY_EMAIL);
    dummyCandidate.put("name", candidateEmail);

    dummyRecruiter.put("email", DUMMY_EMAIL);
    dummyRecruiter.put("name", recruiterName);
    dummyRecruiter.put("status", "yes");
    JSONObject interviewersJson = calendardataJson(attendeeEmailList, null, start, end, organizerCalendarId, title, description, location, dummyCandidate, conference);
    JSONObject guestJson = null;
    //System.out.println("Inteviewers Json: " + interviewersJson.toString());
    Map<String, Object> result = new HashMap();
    if (gmeet && providerType.equals("gmail")) {
      EntryPoint entryPoint = googleCreateApi(eventId, title, start, end, guestEmails, attendeeIds, description, location, applicationContextService.getEmail(), applicationContextService.getProviderRefreshToken(), timezone, null, true, null);
      try {
        Thread.sleep(50);
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
      }
      if (entryPoint != null && entryPoint.getUri() != null) {
        Map conferenceMap = conferenceMap(entryPoint.getPin(), entryPoint.getLabel(), entryPoint.getUri());
        result.put("conferencing", conferenceMap);
        guestJson = calendardataJson(null, guestEmails, start, end, defaultCalendarId, externalTitle, externalDescription, externalLocation, dummyRecruiter, conferenceMap);
        ResponseEntity<?> candidateResponse = RestTemplateUtil.restTemplateUtil(defaultToken, guestJson.toString(), CREATE_EVENT, HttpMethod.POST, CalendarEvent.class);
        Gson gson = new Gson();
        CalendarEvent candidateEventData = (CalendarEvent) candidateResponse.getBody();
        Event candidateEvent = new Event(candidateEventData.getId(), defaultCalendarId, defaultAccountId, gson.toJson(candidateEventData), candidateUUID, ObjectType.EVENT, eventId, EventType.EXTERNAL, timezone);
        calendarDao.saveEvent(null, candidateEvent);
        updateCursorId(defaultToken, organizerToken, defaultUserId, null);
        //updateNylasEvent(organizerToken, defaultUserId, defaultToken)
      }
    } else {
      guestJson = calendardataJson(null, guestEmails, start, end, defaultCalendarId, externalTitle, externalDescription, externalLocation, dummyRecruiter, extConference);
      System.out.println("Guest json: "+guestJson);
      ResponseEntity<?> response = RestTemplateUtil.restTemplateUtil(organizerToken, interviewersJson.toString(), CREATE_EVENT, HttpMethod.POST, CalendarEvent.class);
      updateCursorId(defaultToken, organizerToken, defaultUserId, userId);

      ResponseEntity<?> candidateResponse = RestTemplateUtil.restTemplateUtil(defaultToken, guestJson.toString(), CREATE_EVENT, HttpMethod.POST, CalendarEvent.class);
      updateCursorId(defaultToken, organizerToken, defaultUserId, null);


      Gson gson = new Gson();
      CalendarEvent calendarData = (CalendarEvent) response.getBody();
      CalendarEvent candidateEventData = (CalendarEvent) candidateResponse.getBody();

      Event event = new Event(calendarData.getId(), organizerCalendarId, accountId, gson.toJson(calendarData) , uuid, ObjectType.EVENT, eventId, EventType.INTERNAL, timezone);
      Event candidateEvent = new Event(candidateEventData.getId(), defaultCalendarId, defaultAccountId, gson.toJson(candidateEventData) , candidateUUID, ObjectType.EVENT, eventId, EventType.EXTERNAL, timezone);
      calendarDao.saveEvent(event,candidateEvent);
    }
    System.out.println("Guest Json: " + guestJson.toString());

    result.put("success", "true");
    return result;
  }

  String getTimeZone(String start) {
    ZonedDateTime zonedDateTime = ZonedDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME);
    String timezone = TimeZone.getTimeZone(zonedDateTime.getZone().getId()).getID();
    return timezone;
  }

  String getCursorId(String token) throws IOException {
    ResponseEntity<?> response = RestTemplateUtil.restTemplateUtil(token, null, FETCH_LATEST_CURSOR, HttpMethod.POST, String.class);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(response.getBody().toString());
    String cursorId = null;
    if(response.getStatusCodeValue() != 200) {
      return cursorId;
    }
    return root.get("cursor").asText();
  }

  void updateCursorId(String defaultToken, String organizerToken, String defaultUserId, String userId) throws IOException {
    if(defaultToken.equals(organizerToken) || userId == null) {
      String defaultCursorId = getCursorId(defaultToken);
      calendarDao.updateCursorId(defaultCursorId, defaultUserId);
    }else {
      String organizerCursorId= getCursorId(organizerToken);
      calendarDao.updateCursorId(organizerCursorId, userId);
    }
  }

  JSONObject calendardataJson(Set<Map> participants, Set<String> guestEmails, String start, String end, String calendar_Id, String title, String description, String location,
                              Map<String, Object> dummyRecruiter, Map conferenceMap) {

    long startTime = ZonedDateTime.parse(start).toInstant().getEpochSecond();
    long endTime = ZonedDateTime.parse(end).toInstant().getEpochSecond();
    Map<String, Object> m = new HashMap();
    Map<String, Object> timeObject = new HashMap();
    Set<Map<String, Object>> participantsList = new HashSet<>();
    if(guestEmails!=null && !guestEmails.isEmpty()){
     for(String email:guestEmails) {
      Map<String, Object> participant = new HashMap<>();
      participant.put("email", email);
      participantsList.add(participant);
    }
    }
    if(participants!=null && !participants.isEmpty()){
      for(Map map:participants) {
        Map<String, Object> participant = new HashMap<>();
        participant.put("email", map.get("email"));
        participant.put("name", map.get("name"));
        participantsList.add(participant);
      }
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
    m.put("conferencing", conferenceMap);
    /*if(location!=null){
    m.put("conferenceData", null);
    }*/
    JSONObject json = new JSONObject(m);
    return json;
  }

  String getCalendarId(String token) throws IOException {

    ResponseEntity<?> response = RestTemplateUtil.restTemplateUtil(token, null, FETCH_CALENDAR_ID, HttpMethod.GET, String.class);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(response.getBody().toString());
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

  public Object updateEvent(String eventId, String title, String externalTitle, String start, String end, final Set<String> guestEmails, final Set<EmployeeQueryRequest> attendeeIds, String description,
                           String externalDescription, String location, String externalLocation, Map conferenceMap, Boolean gmeet, Map extConference) throws Exception {
    String default_calendar_Id;
    String organizer_calendar_Id;
    String userId = applicationContextService.getCurrentUserId();
    String defaultUserId = applicationContextService.getDefaultUserId();
    String recruiterName = applicationContextService.getCurrentUsername();
    String organizerToken = applicationContextService.geToken();
    String defaultToken = applicationContextService.getDefaultToken();
    Map<String, String> calendarIdsMap = calendarDao.mapEvent(eventId);
    Map<String, Object> result = new HashMap();
    if(defaultToken.equals(organizerToken)) {
      organizer_calendar_Id = getCalendarId(organizerToken);
      default_calendar_Id = organizer_calendar_Id;
    } else {
      organizer_calendar_Id = getCalendarId(organizerToken);
      default_calendar_Id = getCalendarId(defaultToken);
    }
    String timezone = getTimeZone(start);
    Set<Map> attendeeEmailList = new HashSet<>();
    for(EmployeeQueryRequest attendee:attendeeIds) {
      Map map = new HashMap();
      map.put("email",attendee.getEmail());
      map.put("name",attendee.getFirstName());
      attendeeEmailList.add(map);
    }

    String candidateEmail=null;
    Iterator iterator = guestEmails.iterator();
    while(iterator.hasNext()) {
      candidateEmail = (String) iterator.next();
    }

    Map<String, Object> dummyRecruiter = new HashMap();
    Map<String, Object> dummyCandidate = new HashMap();

    dummyCandidate.put("email", DUMMY_EMAIL);
    dummyCandidate.put("name", candidateEmail);
    dummyCandidate.put("status", "yes");

    dummyRecruiter.put("email", DUMMY_EMAIL);
    dummyRecruiter.put("name", recruiterName);
    dummyRecruiter.put("status", "yes");
    String externalEventUrl = UPDATE_EVENT.replace("{id}", calendarIdsMap.get("EXTERNAL"));
    System.out.println("conference:"+conferenceMap);
    ConferenceData conferenceData = null;
    if (calendarIdsMap.get("INTERNAL") == null || (gmeet && (conferenceMap == null || conferenceMap.get("provider") == null || !conferenceMap.get("provider").equals("Google Meet") ) )) {
      String googleEventId = null;
      Boolean createReq = false;
      if (calendarIdsMap.get("INTERNAL") != null) {
        googleEventId = getGoogleIdByEventId(eventId, applicationContextService.getProviderRefreshToken(), applicationContextService.getEmail());
        createReq = true;
      } else {
        GoogleEvent gEvent = googleEventMapper.getByEventId(eventId);
        googleEventId = gEvent.getGoogleEventId();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        createReq = (gEvent.getMeetLink() == null && gmeet) ? true : false;
        GoogleCreateEventRequestBody gce = mapper.readValue(gEvent.getEventDetails(), GoogleCreateEventRequestBody.class);
        if (conferenceMap != null && conferenceMap.get("provider") != null ) {
          conferenceData = getConferenceData(conferenceMap);
        }
        if (gmeet) {
          conferenceData = gce.getConferenceData();
        }
        if (conferenceData != null) {
          conferenceData.setCreateRequest(null);
        }
        conferenceData = gmeet ? conferenceData : null;
      }
      EntryPoint entryPoint = googleCreateApi(eventId, title, start, end, guestEmails, attendeeIds, description, location, applicationContextService.getEmail(), applicationContextService.getProviderRefreshToken(), timezone, googleEventId, createReq, conferenceData);
      try {
        Thread.sleep(50);
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
      }
      if ((conferenceMap == null && !gmeet) || (entryPoint != null && entryPoint.getUri() != null)) {
        if (entryPoint != null && entryPoint.getUri() != null) {
          conferenceMap = conferenceMap(entryPoint.getPin(), entryPoint.getLabel(), entryPoint.getUri());
          result.put("conferencing", conferenceMap);
        }
        JSONObject guestJson = calendardataJson(null, guestEmails, start, end, default_calendar_Id, externalTitle, externalDescription, externalLocation, dummyRecruiter, conferenceMap);
        ResponseEntity<?> externalResponse = RestTemplateUtil.restTemplateUtil(defaultToken, guestJson.toString(), externalEventUrl, HttpMethod.PUT, CalendarEvent.class);
        updateCursorId(defaultToken, organizerToken, defaultUserId, null);
        Gson gson = new Gson();
        CalendarEvent externalEventData = (CalendarEvent)externalResponse.getBody();
        calendarDao.updateEvent(eventId, null, gson.toJson(externalEventData));
      }
    } else {

      String internalEventUrl = UPDATE_EVENT.replace("{id}", calendarIdsMap.get("INTERNAL"));

      JSONObject guestJson = calendardataJson(null, guestEmails, start, end, default_calendar_Id, externalTitle, externalDescription, externalLocation, dummyRecruiter, extConference);
      JSONObject interviewersJson = calendardataJson(attendeeEmailList, null, start, end, organizer_calendar_Id, title, description, location, dummyCandidate, conferenceMap);

      ResponseEntity<?> internalResponse = RestTemplateUtil.restTemplateUtil(organizerToken, interviewersJson.toString(), internalEventUrl, HttpMethod.PUT, CalendarEvent.class);
      updateCursorId(defaultToken, organizerToken, defaultUserId, userId);

      ResponseEntity<?> externalResponse = RestTemplateUtil.restTemplateUtil(defaultToken, guestJson.toString(), externalEventUrl, HttpMethod.PUT, CalendarEvent.class);
      updateCursorId(defaultToken, organizerToken, defaultUserId, null);


      Gson gson = new Gson();
      CalendarEvent internalEventData = (CalendarEvent) internalResponse.getBody();
      CalendarEvent externalEventData = (CalendarEvent) externalResponse.getBody();
      calendarDao.updateEvent(eventId, gson.toJson(internalEventData), gson.toJson(externalEventData));
    }
    result.put("success", "true");
    return result;
  }

  private ConferenceData getConferenceData(Map conferenceMap) {
    ConferenceData conferenceData = null;
    if (conferenceMap != null && conferenceMap.get("provider") != null) {
      List<EntryPoint> entryPoints = new ArrayList<>();
      EntryPoint entryPoint = new EntryPoint();
      entryPoint.setEntryPointType("video");
      Map detailsMap = (Map)conferenceMap.get("details");
      String uri = String.valueOf(detailsMap.get("url"));
      entryPoint.setUri(uri);
      entryPoint.setLabel(uri.substring(8));
      entryPoints.add(entryPoint);
      Map conferenceSolution = new HashMap();
      Map keyMap = new HashMap();
      conferenceSolution.put("name", conferenceMap.get("provider"));
      keyMap.put("type", "addOn");
      conferenceSolution.put("key", keyMap);
      conferenceData = new ConferenceData();
      conferenceData.setConferenceSolution(conferenceSolution);
      conferenceData.setEntryPoints(entryPoints);
      System.out.println("Conference Data: "+conferenceData);
    }
    return conferenceData;
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

    ResponseEntity<?> response = RestTemplateUtil.restTemplateUtil(organizerToken, json.toString(), CHECK_AVAILABILITY, HttpMethod.POST, String.class);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(response.getBody().toString());
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

    ResponseEntity<?> internalResponse = RestTemplateUtil.restTemplateUtil(organizerToken, null, internalEventUrl, HttpMethod.DELETE, String.class);
    updateCursorId(defaultToken, organizerToken, defaultUserId, userId);

    ResponseEntity<?> externalResponse = RestTemplateUtil.restTemplateUtil(defaultToken, null, externalEventUrl, HttpMethod.DELETE, String.class);
    updateCursorId(defaultToken, organizerToken, defaultUserId, null);

    calendarDao.deleteEvent(id);
  }


  public EntryPoint googleCreateApi(String eventId, String title, String start, String end, final Set<String> guestEmails, final Set<EmployeeQueryRequest> attendeeIds, String description,String location, String email, String refreshToken, String timezone, String googleId, Boolean createReq, ConferenceData conferenceData) throws Exception {
    String token = getAccessToken(refreshToken);
    EntryPoint result = new EntryPoint();
    if (token != null) {
      GoogleCreateEventRequestBody gce = new GoogleCreateEventRequestBody();
      String requestId = UUID.randomUUID().toString().replace("-", "");
      if (createReq) {
        CreateRequest createRequest = new CreateRequest();
        createRequest.setRequestId(requestId);
        ConferenceData cf = new ConferenceData();
        cf.setCreateRequest(createRequest);
        gce.setConferenceData(cf);
      }
      if (googleId != null && !createReq && conferenceData != null) {
        gce.setConferenceData(conferenceData);
      }
      gce.setKind("calendar#event");
      gce.setEventType("default");
      gce.setSummary(title);
      gce.setLocation(location);
      gce.setDescription(description);
      DateObj startObj = new DateObj();
      DateObj endObj = new DateObj();
      ZonedDateTime startz = ZonedDateTime.parse(start, DateTimeFormatter.ISO_ZONED_DATE_TIME);
      ZonedDateTime endz = ZonedDateTime.parse(end, DateTimeFormatter.ISO_ZONED_DATE_TIME);
      startObj.setDateTime(startz.withZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME));
      endObj.setDateTime(endz.withZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME));
      gce.setStart(startObj);
      gce.setEnd(endObj);
      List<Organizer> attendees = new ArrayList();
      if (attendeeIds != null) {
        for (EmployeeQueryRequest attendee : attendeeIds) {
          Organizer guest = new Organizer();
          guest.setDisplayName(attendee.getFullName());
          guest.setEmail(attendee.getEmail());
          attendees.add(guest);
        }
        gce.setAttendees(attendees);
      }
      String meetLink = null;

      String uri = GOOGLE_CREATE_EVENT_API;
      HttpMethod method = HttpMethod.POST;
      if (googleId != null) {
        uri = GOOGLE_UPDATE_EVENT_API.replace("{eventId}", googleId);
        method = HttpMethod.PUT;
      }
      uri = uri.replace("{calendarId}", email);
      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders headers = new HttpHeaders();
      headers.add("Authorization", "Bearer " + token);
      HttpEntity<Map<String, String>> request = new HttpEntity<>(objectToMap(gce), headers);
      //ResponseEntity<?> response = restTemplate.postForEntity(uri, request, Map.class);
      System.out.println("Request body: "+objectToMap(gce));
      ResponseEntity<?> response = restTemplate.exchange(uri, method, request, Map.class);
      System.out.println("response: "+response);
      System.out.println("response body: "+response.getBody());
      Map map = (Map) response.getBody();
      if (map.get("conferenceData") != null ) {
        Map conferenceMap = (Map) map.get("conferenceData");
        if (conferenceMap.get("entryPoints") != null ) {
          List<Map> entryPoints = (List<Map>) conferenceMap.get("entryPoints");
          String conferenceId = (String) map.get("conferenceId");
          for (Map entryPointMap : entryPoints) {
            EntryPoint entryPoint = (EntryPoint) mapToObject(entryPointMap, EntryPoint.class);
            if (entryPoint.getEntryPointType().equals("video")) {
              result.setUri(entryPoint.getUri());
              meetLink = entryPoint.getUri();
            } else if (entryPoint.getEntryPointType().equals("phone")) {
              result.setLabel(entryPoint.getLabel());
              result.setPin(entryPoint.getPin());
            }
          }
        }
      }
      Gson gson = new Gson();
      if (googleId != null) {
        googleEventMapper.updateGoogleEvent(googleId, gson.toJson(map), meetLink, timezone);
      } else {
        googleEventMapper.saveGoogleEvent(applicationContextService.getAccountId(), String.valueOf(map.get("id")), gson.toJson(map), meetLink, applicationContextService.getCurrentUserId(), timezone, eventId, requestId);
      }
    }
    return result;
  }

  private String getAccessToken(String refreshToken) {
    String token = null;
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    Map map = new HashMap();
    map.put("grant_type", "refresh_token");
    map.put("refresh_token", refreshToken);
    map.put("client_secret", System.getenv(GOOGLE_CLIENT_SECRET));
    map.put("client_id", System.getenv(GOOGLE_CLIENT_ID));
    String uri = GOOGLE_TOKEN_API;
    HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
    ResponseEntity<?> response = restTemplate.postForEntity(uri, request, Map.class);
    Map resMap = (Map)response.getBody();
    if (resMap != null) {
      token = String.valueOf(resMap.get("access_token"));
    }
    return token;
  }

  private Map conferenceMap(String pin, String phone, String uri) {
    Set<String> phones = new HashSet<>();
    phones.add(phone);
    Map map = new HashMap();
    map.put("pin", pin);
    map.put("phone", phones);
    map.put("url", uri);
    Map details = new HashMap();
    details.put("details", map);
    details.put("provider", "Google Meet");
    return details;
  }

  private String getGoogleIdByEventId(String eventId, String refreshToken, String email) {
    String token = getAccessToken(refreshToken);
    String googleId = null;
    if (token != null) {
      String uri = GOOGLE_GET_EVENTS_API.replace("{calendarId}", email);
      uri = uri+"?q="+eventId;
      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders headers = new HttpHeaders();
      headers.add("Authorization", "Bearer " + token);
      HttpEntity<Map<String, String>> request = new HttpEntity<>(null, headers);
      ResponseEntity<?> response = restTemplate.exchange(uri, HttpMethod.GET, request, Map.class);
      List items = (List)((Map)response.getBody()).get("items");
      if (items != null && !items.isEmpty()) {
        googleId = objectToMap(items.get(0)).get("id");
      }
    }
    return googleId;
  }

}
