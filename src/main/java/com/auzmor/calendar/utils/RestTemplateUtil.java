package com.auzmor.calendar.utils;

import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class RestTemplateUtil {

  public static ResponseEntity<String> restTemplateUtil(String token, String body, String url, HttpMethod method) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    byte[] plainCredsBytes = token.getBytes();
    String base64Creds = java.util.Base64.getEncoder().encodeToString(plainCredsBytes);
    headers.add("Authorization", "Basic "+ base64Creds);
    HttpEntity<String> request = new HttpEntity<String>(body, headers);
    return restTemplate.exchange(url, method, request, String.class);
  }
}