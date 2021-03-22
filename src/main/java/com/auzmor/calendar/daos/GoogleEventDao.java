package com.auzmor.calendar.daos;

import java.util.Set;

public interface GoogleEventDao {
  Set<String> getByGmeets(Set<String> gmeets);
}
