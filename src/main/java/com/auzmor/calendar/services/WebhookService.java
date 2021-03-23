package com.auzmor.calendar.services;

import org.springframework.stereotype.Service;

@Service
public interface WebhookService {
  void handleWebhook(String date, String objectId, String eventType, String object, String accountId) throws Exception;

  void handleEventCreation(String objectId, String eventType, String object, String accountId) throws Exception;
}
