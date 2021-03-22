package com.auzmor.calendar.controllers;

import com.auzmor.calendar.models.UserAccount;
import com.auzmor.calendar.services.AccountService;
import com.auzmor.calendar.services.WebhookService;
import io.swagger.annotations.ApiOperation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class WebhookController {

  @Autowired
  private WebhookService webhookService;

  @Autowired
  private AccountService accountService;

  @RequestMapping(value = "/webhook", method = RequestMethod.GET)
  public String test(HttpServletRequest request) {
    return request.getParameter("challenge");
  }

  @RequestMapping(value = "/webhook/create", method = RequestMethod.GET)
  public String testCreate(HttpServletRequest request) {
    return request.getParameter("challenge");
  }

  @RequestMapping(value = "/webhook", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public String testPost(HttpServletRequest request) throws Exception {
    String details = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    JSONObject jo = new JSONObject(details);
    JSONArray delta = (JSONArray)jo.get("deltas");
    JSONObject diff = (JSONObject) delta.get(0);
    JSONObject objectData = (JSONObject)diff.get("object_data");
    webhookService.handleWebhook(diff.get("date").toString(), objectData.get("id").toString(), diff.get("type").toString(), diff.get("object").toString(), objectData.get("account_id").toString());
    return request.getParameter("challenge");
  }

  @RequestMapping(value = "/webhook/create", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public void testCreatePost(HttpServletRequest request) throws Exception {
    String details = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    JSONObject jo = new JSONObject(details);
    JSONArray delta = (JSONArray)jo.get("deltas");
    JSONObject diff = (JSONObject) delta.get(0);
    JSONObject objectData = (JSONObject)diff.get("object_data");
    //webhookService.handleWebhook(diff.get("date").toString(), objectData.get("id").toString(), diff.get("type").toString(), diff.get("object").toString(), objectData.get("account_id").toString());
  }


  @ApiOperation(value = "Add nylasAccount ")
  @RequestMapping(value = "/addNylasAccount", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  // @PreAuthorize("@customSecurityService.hasPermission(authentication, '" + PermissionConstant.ADMIN_PERMISSION + "', '" + PermissionConstant.CREATE_CANDIDATE_PERMISSION + "')")
  public ResponseEntity<Object> addNylasAccount(@RequestBody @Valid UserAccount account) throws Exception {
    accountService.addNylasAccount(account);
    return new ResponseEntity<>(new HttpHeaders(), HttpStatus.NO_CONTENT);
  }

}
