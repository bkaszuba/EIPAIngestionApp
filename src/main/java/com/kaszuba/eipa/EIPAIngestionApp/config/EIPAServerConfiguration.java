package com.kaszuba.eipa.EIPAIngestionApp.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

public class EIPAServerConfiguration {

  private String endpoint;

  @JsonProperty("api-key")
  private String apiKey;

  public EIPAServerConfiguration() {
  }

  public EIPAServerConfiguration(String endpoint, String apiKey) {
    this.endpoint = endpoint;
    this.apiKey = apiKey;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getEndpointWithApiKey() {
    return String.format("%s/%s", endpoint, apiKey);
  }

  public boolean checkEmptyFields() {
    return StringUtils.isAnyEmpty(endpoint, apiKey);
  }
}
