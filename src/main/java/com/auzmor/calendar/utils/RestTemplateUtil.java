package com.auzmor.calendar.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
    System.out.println("body: "+body);
    HttpEntity<String> request = new HttpEntity<String>(body, headers);
    return restTemplate.exchange(url, method, request, classType);
  }

  public static Map<String, String> objectToMap(Object object){
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mapper.registerModule(new JavaTimeModule());
    return mapper.convertValue(object, Map.class);
  }

  public static Object mapToObject(Map map, Class cls) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.registerModule(new JavaTimeModule());
    return mapper.convertValue(map, cls);
  }
}
