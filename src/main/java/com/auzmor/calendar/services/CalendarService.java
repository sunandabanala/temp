package com.auzmor.calendar.services;

import com.auzmor.calendar.controllers.requests.events.AttendeeRequest;
import com.auzmor.calendar.controllers.requests.events.EmployeeQueryRequest;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Service
public interface CalendarService {

  Object saveEvent(final String eventId, final String title, final String externalTitle, final String start, final String end, final Set<String> guestEmails, final Set<EmployeeQueryRequest> attendeeIds,
                  final String description, final  String externalDescription, final String location, final String externalLocation, final Boolean gmeet) throws Exception;

  Object updateEvent(final String eventId, final String title, final String externalTitle, final String start, final String end, final Set<String> guestEmails, final Set<EmployeeQueryRequest> attendeeIds,
                   final String description, final  String externalDescription, final String location, final String externalLocation, Map conferenceMap) throws JSONException, IOException;

  Object checkAvailability(String email, final long start, final long end) throws IOException;

  void deleteEvent(final String id) throws IOException;

}

