package com.auzmor.calendar.daos;

import com.auzmor.calendar.models.entities.GoogleEvent;

import java.util.Set;

public interface GoogleEventDao {
  GoogleEvent getByGmeet(String gmeet);
}
