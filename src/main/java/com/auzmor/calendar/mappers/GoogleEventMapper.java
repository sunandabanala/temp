package com.auzmor.calendar.mappers;

import com.auzmor.calendar.models.entities.GoogleEvent;
import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

@Mapper
public interface GoogleEventMapper {
  GoogleEvent getByGmeet(String gmeet);
  void saveGoogleEvent(String accountId, String googleEventId, String eventDetails, String meetLink, String userId, String timezone, String platformEventId);

}
