package com.kaszuba.eipa.EIPAIngestionApp.model.received;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Point {

  @JsonProperty("point_id")
  private Integer pointId;

  @JsonProperty("status")
  private Status status;

  public Point() {
  }

  public Point(int pointId, Status status) {
    this.pointId = pointId;
    this.status = status;
  }

  public Integer getPointId() {
    return pointId;
  }

  public void setPointId(Integer pointId) {
    this.pointId = pointId;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }
}
