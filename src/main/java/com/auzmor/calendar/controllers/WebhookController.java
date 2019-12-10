package com.auzmor.calendar.controllers;

import com.auzmor.calendar.services.WebhookService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@RestController
public class WebhookController {

  @Autowired
  private WebhookService webhookService;

  @RequestMapping(value = "/webhook", method = RequestMethod.GET)
  public String test(HttpServletRequest request) {
    return request.getParameter("challenge");
  }

  @RequestMapping(value = "/webhook", method = RequestMethod.POST)
  public String testPost(HttpServletRequest request) throws Exception {
    String details = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    JSONObject jo = new JSONObject(details);
    JSONArray delta = (JSONArray)jo.get("deltas");
    JSONObject diff = (JSONObject) delta.get(0);
    JSONObject objectData = (JSONObject)diff.get("object_data");
    webhookService.handleWebhook(diff.get("date").toString(), objectData.get("id").toString(), diff.get("type").toString(), diff.get("object").toString(), objectData.get("account_id").toString());
    return request.getParameter("challenge");
  }



}
