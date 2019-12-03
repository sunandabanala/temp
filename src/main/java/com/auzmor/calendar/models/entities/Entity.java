package com.auzmor.calendar.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Entity {
  protected Entity(final String uuid) {
    this.uuid = uuid;
  }

  protected String uuid;
  protected Date createdAt;
  protected Date updatedAt;
}
