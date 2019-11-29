package com.auzmor.calendar.services.impls;

import com.auzmor.calendar.daos.CalendarDao;
import com.auzmor.calendar.models.Event;
import com.auzmor.calendar.services.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CalendarServiceImpl implements CalendarService {

  @Autowired
  CalendarDao calendarDao;

  @Override
  public Event saveEvent() {
    String uuid = UUID.randomUUID().toString().replace("-", "");
    Event e1 = new Event("1","2","3",null, uuid);
    System.out.println(e1.getUuid());
    calendarDao.saveEvent(e1);
    return null;
  }
}
