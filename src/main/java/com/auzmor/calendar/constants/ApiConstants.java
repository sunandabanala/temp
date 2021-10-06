package com.auzmor.calendar.constants;

public class ApiConstants {
  public static final String PLATFORM_EVENT_API = "/updateEvent";
  public static String GOOGLE_CREATE_EVENT_API = "https://www.googleapis.com/calendar/v3/calendars/{calendarId}/events?conferenceDataVersion=1&sendUpdates=all";
  public static String GOOGLE_UPDATE_EVENT_API = "https://www.googleapis.com/calendar/v3/calendars/{calendarId}/events/{eventId}?conferenceDataVersion=1&sendUpdates=all";
  public static String GOOGLE_TOKEN_API = "https://www.googleapis.com/oauth2/v4/token";
  public static String GOOGLE_GET_EVENTS_API = "https://www.googleapis.com/calendar/v3/calendars/{calendarId}/events";
  public static String GOOGLE_DELETE_EVENT_API = "https://www.googleapis.com/calendar/v3/calendars/{calendarId}/events/{eventId}?sendUpdates=all";
}
