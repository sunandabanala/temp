package com.auzmor.calendar.services;

import org.springframework.stereotype.Service;

@Service
public interface AsyncService {
  void saveGoogleEvent(String nylasEventId, String token, String platformEventId, String cursorId, String timezone, String userId);
}
