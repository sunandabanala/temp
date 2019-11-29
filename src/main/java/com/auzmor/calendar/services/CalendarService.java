package com.auzmor.calendar.services;

import com.auzmor.calendar.models.Event;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CalendarService {

  Event saveEvent();
}
