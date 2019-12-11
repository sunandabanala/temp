package com.auzmor.calendar.services;

import com.auzmor.calendar.controllers.requests.events.AttendeeRequest;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;

@Service
public interface CalendarService {

  Object saveEvent(final String eventId, final String title, final String externalTitle, final long start, final long end, final Set<String> guestEmails, final Set<AttendeeRequest> attendeeIds,
                  final String description, final  String externalDescription, final String location) throws JSONException, IOException;

  Object updateEvent(final String eventId, final String title, final String externalTitle, final long start, final long end, final Set<String> guestEmails, final Set<AttendeeRequest> attendeeIds,
                   final String description, final  String externalDescription, final String location) throws JSONException, IOException;

  Object checkAvailability(String email, final long start, final long end) throws IOException;

  void deleteEvent(final String id) throws IOException;

}

