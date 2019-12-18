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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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

  @ApiOperation(value = "Add nylasAccount ")
  @RequestMapping(value = "/addNylasAccount", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  // @PreAuthorize("@customSecurityService.hasPermission(authentication, '" + PermissionConstant.ADMIN_PERMISSION + "', '" + PermissionConstant.CREATE_CANDIDATE_PERMISSION + "')")
  public ResponseEntity<Object> addNylasAccount(@RequestBody @Valid UserAccount account) throws Exception {
    accountService.addNylasAccount(account);
    return new ResponseEntity<>(new HttpHeaders(), HttpStatus.NO_CONTENT);
  }



}
