package com.auzmor.calendar.daos;

import com.auzmor.calendar.models.Event;
import org.springframework.stereotype.Component;

@Component
public interface CalendarDao {

  Event saveEvent(Event event);
}
