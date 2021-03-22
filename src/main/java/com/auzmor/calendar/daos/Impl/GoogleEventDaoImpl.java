package com.auzmor.calendar.daos.Impl;

import com.auzmor.calendar.daos.GoogleEventDao;
import com.auzmor.calendar.mappers.GoogleEventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class GoogleEventDaoImpl implements GoogleEventDao {
  @Autowired
  private GoogleEventMapper googleEventMapper;

  public Set<String> getByGmeets(Set<String> gmeets) {
    return googleEventMapper.getByGmeets(gmeets);
  }
}
