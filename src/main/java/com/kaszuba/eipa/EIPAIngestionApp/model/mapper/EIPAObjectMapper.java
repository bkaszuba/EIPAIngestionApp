package com.kaszuba.eipa.EIPAIngestionApp.model.mapper;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class EIPAObjectMapper {

  private static ObjectMapper mapper;

  private EIPAObjectMapper() {
  }

  public static synchronized ObjectMapper getMapper() {
    if (mapper == null) {
      mapper = (new ObjectMapper())
        .registerModule(new JavaTimeModule())
        .setSerializationInclusion(Include.NON_NULL)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
    return mapper;
  }
}
