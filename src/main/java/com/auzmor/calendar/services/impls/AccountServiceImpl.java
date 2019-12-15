package com.auzmor.calendar.services.impls;

import com.auzmor.calendar.daos.AccountDao;
import com.auzmor.calendar.models.UserAccount;
import com.auzmor.calendar.services.AccountService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static com.auzmor.calendar.constants.ApiConstants.GET_LATEST_CURSOR_URI;

@Service
public class AccountServiceImpl implements AccountService {

  @Autowired
  private AccountDao accountDao;

  @Override
  public UserAccount getAccount(String nylasAccountId) {
    return accountDao.getAccount(nylasAccountId);
  }

  @Override
  public void addNylasAccount(UserAccount userAccount) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    String nylas_token = "YLwefgahbQ3ezFLEyHm5zgDlYVMcER:";
    byte[] plainCredsBytes = nylas_token.getBytes();
    byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
    String base64Creds = new String(base64CredsBytes);
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setCacheControl("no-cache");
    headers.add("Authorization", "Basic " + base64Creds);
    HttpEntity<String> request = new HttpEntity<>(null, headers);
    ResponseEntity<Map> response = restTemplate.postForEntity(GET_LATEST_CURSOR_URI, request, Map.class);
    String cursor = (String) response.getBody().get("cursor");
    userAccount.setCursorId(cursor);
    accountDao.addNylasAccount(userAccount);
  }

}
