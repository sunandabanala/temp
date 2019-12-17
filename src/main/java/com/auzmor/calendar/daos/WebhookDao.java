package com.auzmor.calendar.daos;

import org.springframework.stereotype.Component;

@Component
public interface WebhookDao {
  void handleWebhook(String cursorId, String token, String accountId) throws Exception;
}
