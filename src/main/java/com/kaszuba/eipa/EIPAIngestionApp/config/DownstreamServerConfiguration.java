package com.kaszuba.eipa.EIPAIngestionApp.config;

import org.apache.commons.lang3.StringUtils;

public class DownstreamServerConfiguration {

  public String endpoint;

  public DownstreamServerConfiguration() {
  }

  public DownstreamServerConfiguration(String endpoint) {
    this.endpoint = endpoint;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public boolean checkEmptyFields() {
    return StringUtils.isEmpty(endpoint);
  }
}
