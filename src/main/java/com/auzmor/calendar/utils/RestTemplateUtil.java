package com.auzmor.calendar.utils;

import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class RestTemplateUtil {

  public static ResponseEntity<?> restTemplateUtil(String token, String body, String url, HttpMethod method, Class<?> classType) {
    RestTemplate restTemplate = new RestTemplate();
    String tokenValue;
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    if (token != null) {
      tokenValue = token+":";
      byte[] plainCredsBytes = tokenValue.getBytes();
      String base64Creds = java.util.Base64.getEncoder().encodeToString(plainCredsBytes);
      headers.add("Authorization", "Basic " + base64Creds);
    }
    HttpEntity<String> request = new HttpEntity<String>(body, headers);
    return restTemplate.exchange(url, method, request, classType);
  }
}
