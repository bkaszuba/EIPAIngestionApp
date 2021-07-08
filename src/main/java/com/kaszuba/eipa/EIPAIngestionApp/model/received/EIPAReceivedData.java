package com.kaszuba.eipa.EIPAIngestionApp.model.received;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EIPAReceivedData {

  @JsonProperty("data")
  private List<Point> points;

  public EIPAReceivedData() {
  }

  public EIPAReceivedData(List<Point> points) {
    this.points = points;
  }

  public List<Point> getPoints() {
    return points;
  }

  public void setPoints(List<Point> points) {
    this.points = points;
  }
}
