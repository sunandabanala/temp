package com.auzmor.calendar.services;

import com.auzmor.calendar.controllers.requests.events.AttendeeRequest;
import com.auzmor.calendar.models.entities.Event;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;

@Service
public interface CalendarService {

  Event saveEvent(final String title, final String username, final long start, final long end, final Set<AttendeeRequest> attendeeIds,
  final String description, final String location, final String type, final String url) throws JSONException, IOException;

  Event updateEvent(final String id, final String title, final String username, final long start, final long end, final Set<AttendeeRequest> attendeeIds,
                  final String description, final String location, final String type, final String url) throws JSONException, IOException;

  Object checkAvailability(String email, final long start, final long end) throws IOException;

  void deleteEvent(final String id);
}

