package com.kaszuba.eipa.EIPAIngestionApp.model.received;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import java.time.ZonedDateTime;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "status")
public class Status {

  private int availability;
  private int status;
  private ZonedDateTime ts;

  public Status() {
  }

  public Status(int availability, int status, ZonedDateTime ts) {
    this.availability = availability;
    this.status = status;
    this.ts = ts;
  }

  public int getAvailability() {
    return availability;
  }

  public int getStatus() {
    return status;
  }

  public ZonedDateTime getTs() {
    return ts;
  }

  public void setAvailability(int availability) {
    this.availability = availability;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public void setTs(ZonedDateTime ts) {
    this.ts = ts;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Status status1 = (Status) o;
    return availability == status1.availability && Objects.equals(status, status1.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(availability, status);
  }
}
