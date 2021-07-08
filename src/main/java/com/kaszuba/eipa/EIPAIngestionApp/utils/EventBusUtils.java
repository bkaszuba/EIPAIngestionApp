package com.kaszuba.eipa.EIPAIngestionApp.utils;

import static com.kaszuba.eipa.EIPAIngestionApp.model.TopicConstants.HEADER_OPERATION_NAME;

import io.vertx.core.eventbus.DeliveryOptions;

public class EventBusUtils {

  public static DeliveryOptions prepareDeliveryOptions(String operationHeader) {
    return new DeliveryOptions()
      .addHeader(HEADER_OPERATION_NAME, operationHeader);
  }
}
