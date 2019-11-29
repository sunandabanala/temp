package com.auzmor.calendar.services.impls;

import com.auzmor.calendar.services.CalendarService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarServiceImpl implements CalendarService {
  @Override
  public List create() {
    System.out.println("pooja");
    List<?> ABC = new ArrayList<>();
    return ABC;
  }
}
