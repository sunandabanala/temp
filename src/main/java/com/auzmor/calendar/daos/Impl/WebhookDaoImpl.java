package com.auzmor.calendar.daos.Impl;

import com.auzmor.calendar.constants.NylasApiConstants;
import com.auzmor.calendar.daos.AccountDao;
import com.auzmor.calendar.daos.WebhookDao;
import com.auzmor.calendar.models.entities.Event;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WebhookDaoImpl implements WebhookDao {

  @Autowired
  private AccountDao accountDao;

  @Override
  public void createEvent(Event event) {

  }

  @Override
  public void updateEvent(Event event) {

  }

  @Override
  public void deleteEvent(String eventId) {

  }

  @Override
  public void handleWebhook(String cursorId, String token, String accountId) throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    byte[] plainCredsBytes = token.getBytes();
    String base64Creds = java.util.Base64.getEncoder().encodeToString(plainCredsBytes);
    headers.add("Authorization", "Basic "+ base64Creds);
    HttpEntity<String> request = new HttpEntity<String>(headers);
    ResponseEntity<String> response = restTemplate.exchange(NylasApiConstants.FETCH_DELTAS+cursorId, HttpMethod.GET, request , String.class);
    JSONObject jo = new JSONObject(response.getBody());
    String latestCursor = jo.get("cursor_end").toString();
    if (!cursorId.equals(latestCursor)) {
      JSONArray deltas = (JSONArray)jo.get("deltas");
      Map<String, Object> events = new HashMap<>();
      events = processDeltas(deltas);
      System.out.println(deltas);
      accountDao.updateAccount(accountId, latestCursor);
    }
  }

  private Map<String, Object> processDeltas(JSONArray deltas) throws Exception {

    Map<String, Object> events = new HashMap<>();
    for (int i=0; i<deltas.length(); i++) {
      JSONObject jsonObject = deltas.getJSONObject(i);
      events.put(jsonObject.get("id").toString(), jsonObject.get("attributes"));
    }
    return events;
  }

}
