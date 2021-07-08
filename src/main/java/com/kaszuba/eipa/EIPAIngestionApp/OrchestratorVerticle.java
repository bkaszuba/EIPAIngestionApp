package com.kaszuba.eipa.EIPAIngestionApp;

import static com.kaszuba.eipa.EIPAIngestionApp.model.TopicConstants.*;
import static com.kaszuba.eipa.EIPAIngestionApp.utils.EventBusUtils.prepareDeliveryOptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kaszuba.eipa.EIPAIngestionApp.config.ConfigurationVerticle;
import com.kaszuba.eipa.EIPAIngestionApp.config.EIPAIngestionAppConfiguration;
import com.kaszuba.eipa.EIPAIngestionApp.model.mapper.EIPAObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrchestratorVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorVerticle.class);
  private EIPAIngestionAppConfiguration eipaIngestionAppConfiguration;

  public static void main(final String[] args) {
    Launcher.executeCommand("run", ConfigurationVerticle.class.getName());
  }

  @Override
  public void start() {
    readConfig(vertx.getOrCreateContext().config());
    vertx.setPeriodic(eipaIngestionAppConfiguration.getPollingRate(), h -> {
      callForEipaDataAndSendCachedStatuses();
    });
  }

  void callForEipaDataAndSendCachedStatuses() {
    LOGGER.info("Calling HTTP verticle to get EIPA data");
    vertx.eventBus().request(HTTP_VERTICLE_ADDRESS, "", prepareDeliveryOptions(HTTP_VERTICLE_CALL_FOR_DATA), ar -> {
      if (ar.succeeded()) {
        callForCacheAndSendIt();
      }
    });
  }

  private void callForCacheAndSendIt() {
    LOGGER.info("Calling Cache verticle to get cached EIPA data");
    vertx.eventBus().request(CACHE_VERTICLE_ADDRESS, "", prepareDeliveryOptions(CACHE_VERTICLE_GET), ar3 -> {
      if (ar3.succeeded()) {
        LOGGER.info("Received response from Cache verticle with cached EIPA data. Calling HTTP verticle to send it.");
        vertx.eventBus().request(HTTP_VERTICLE_ADDRESS, ar3.result().body(), prepareDeliveryOptions(HTTP_VERTICLE_SEND_DATA));
      }
    });
  }

  private void readConfig(JsonObject config) {
    try {
      eipaIngestionAppConfiguration = EIPAObjectMapper.getMapper().readValue(config.getString("eipa.ingestion.app"), EIPAIngestionAppConfiguration.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Could not read configuration.");
    }
  }
}
