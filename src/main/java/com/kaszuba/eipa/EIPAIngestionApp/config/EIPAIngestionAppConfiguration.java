package com.kaszuba.eipa.EIPAIngestionApp.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EIPAIngestionAppConfiguration {

  @JsonProperty("polling-rate")
  private int pollingRate = 5000;

  public EIPAIngestionAppConfiguration() {
  }

  public EIPAIngestionAppConfiguration(int pollingRate) {
    this.pollingRate = pollingRate;
  }

  public int getPollingRate() {
    return pollingRate;
  }
}
