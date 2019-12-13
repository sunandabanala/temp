package com.auzmor.calendar.daos;

import com.auzmor.calendar.models.entities.Event;
import org.springframework.stereotype.Component;

@Component
public interface WebhookDao {
  void handleWebhook(String cursorId, String token, String accountId) throws Exception;
}
