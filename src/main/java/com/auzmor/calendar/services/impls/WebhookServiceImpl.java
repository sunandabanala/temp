package com.auzmor.calendar.services.impls;

import com.auzmor.calendar.daos.AccountDao;
import com.auzmor.calendar.daos.CalendarDao;
import com.auzmor.calendar.daos.WebhookDao;
import com.auzmor.calendar.models.UserAccount;
import com.auzmor.calendar.services.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class WebhookServiceImpl implements WebhookService {
  @Autowired
  private WebhookDao webhookDao;

  @Autowired
  private CalendarDao calendarDao;

  @Autowired
  private AccountDao accountDao;

  @Override
  public void handleWebhook(String date, String objectId, String eventType, String object, String accountId) throws Exception {
    UserAccount account = accountDao.getAccount(accountId);
    String cursorId = account.getCursorId();
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    String token = account.getNylasToken() + ":";
    webhookDao.handleWebhook(cursorId, token, accountId);
  }
}
