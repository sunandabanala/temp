package com.auzmor.calendar.constants;

public class NylasApiConstants {
  public static final String FETCH_LATEST_CURSOR = "https://api.nylas.com/delta/latest_cursor";

  public static final String FETCH_DELTAS = "https://api.nylas.com/delta?cursor=";

  public static final String CREATE_EVENT = "https://api.nylas.com/events?notify_participants=true";

  public static final String UPDATE_EVENT = "https://api.nylas.com/events/{id}?notify_participants=true";

  public static final String DELETE_EVENT = "https://api.nylas.com/events/{id}?notify_participants=true";

  public static final String CHECK_AVAILABILITY = "https://api.nylas.com/calendars/free-busy";

  public static final String FETCH_CALENDAR_ID ="https://api.nylas.com/calendars";




}
