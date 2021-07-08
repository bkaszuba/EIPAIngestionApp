package com.kaszuba.eipa.EIPAIngestionApp.model.produced;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class EIPAUpdatedStatus {

  private final String id;
  private final Instant ts;
  private final ResponseType type;
  private final List<UpdatedStatus> data;

  public EIPAUpdatedStatus(String id, Instant ts, ResponseType type, List<UpdatedStatus> data) {
    this.id = id;
    this.ts = ts;
    this.type = type;
    this.data = data;
  }

  public EIPAUpdatedStatus(List<UpdatedStatus> data) {
    this.id = UUID.randomUUID().toString();
    this.ts = Instant.now();
    this.type = ResponseType.STATUS_UPDATE;
    this.data = data;
  }

  public String getId() {
    return id;
  }

  public Instant getTs() {
    return ts;
  }

  public ResponseType getType() {
    return type;
  }

  public List<UpdatedStatus> getData() {
    return data;
  }
}
