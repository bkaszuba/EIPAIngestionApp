package com.kaszuba.eipa.EIPAIngestionApp.model.produced;

import java.time.Instant;
import java.time.ZonedDateTime;

public class UpdatedStatus {

  private final int pointId;
  private final StatusType status;
  private final Instant originalTs;

  public UpdatedStatus(int pointId, int status, ZonedDateTime originalTs) {
    this.pointId = pointId;
    this.status = parseToStatusType(status);
    this.originalTs = originalTs.toInstant();
  }

  private StatusType parseToStatusType(int status) {
    return 1 == status ? StatusType.AVAILABLE : StatusType.OCCUPIED;
  }

  public int getPointId() {
    return pointId;
  }

  public StatusType getStatus() {
    return status;
  }

  public Instant getOriginalTs() {
    return originalTs;
  }
}
