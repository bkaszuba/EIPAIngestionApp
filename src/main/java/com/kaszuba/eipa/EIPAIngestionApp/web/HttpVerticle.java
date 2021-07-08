package com.kaszuba.eipa.EIPAIngestionApp.web;

import static com.kaszuba.eipa.EIPAIngestionApp.model.TopicConstants.*;
import static com.kaszuba.eipa.EIPAIngestionApp.utils.EventBusUtils.prepareDeliveryOptions;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.run;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaszuba.eipa.EIPAIngestionApp.config.DownstreamServerConfiguration;
import com.kaszuba.eipa.EIPAIngestionApp.config.EIPAServerConfiguration;
import com.kaszuba.eipa.EIPAIngestionApp.model.mapper.EIPAObjectMapper;
import com.kaszuba.eipa.EIPAIngestionApp.model.produced.EIPAUpdatedStatus;
import com.kaszuba.eipa.EIPAIngestionApp.model.produced.UpdatedStatus;
import com.kaszuba.eipa.EIPAIngestionApp.model.received.Status;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpVerticle.class);
  private static final ObjectMapper mapper = EIPAObjectMapper.getMapper();
  private DownstreamServerConfiguration downstreamServerConfiguration;
  private EIPAServerConfiguration EIPAServerConfiguration;
  private WebClient webClient;

  @Override
  public void start() throws Exception {
    webClient = WebClient.create(vertx);
    readConfig(vertx.getOrCreateContext().config());
    vertx.eventBus().consumer(HTTP_VERTICLE_ADDRESS, this::handleMessage);
  }

  private void handleMessage(Message<Object> message) {
    String operation = message.headers().get(HEADER_OPERATION_NAME);
    LOGGER.info("Received message with operation: {}.", operation);
    Match(operation).of(
      Case($(HTTP_VERTICLE_CALL_FOR_DATA), event -> run(() -> this.callToEipaForData(message))),
      Case($(HTTP_VERTICLE_SEND_DATA), event -> run(() -> this.sendStoredStatuses(message)))
    );
  }

  void callToEipaForData(Message<Object> message) {
    LOGGER.info("Calling EIPA to receive data");
    webClient
      .getAbs(EIPAServerConfiguration.getEndpointWithApiKey())
      .send()
      .onSuccess(res -> {
        if (res.statusCode() == 200 && res.getHeader("content-type").equals("application/json") && !res.bodyAsString().isBlank()) {
          LOGGER.info("Received response with data from EIPA.");
          sendMessageToStoreStatuses(res.bodyAsJsonObject(), message);
        } else {
          LOGGER.warn("Something went wrong " + res.statusCode());
        }
      })
      .onFailure(err ->
        LOGGER.warn("Something went wrong " + err.getMessage()));
  }

  void sendStoredStatuses(Message<Object> message) {
    Map<Integer, Status> statuses = convertJsonToStatuses(message.body().toString());
    LOGGER.info("Going to send {} updated statuses to {}", statuses.size(), downstreamServerConfiguration.getEndpoint());
    webClient
      .postAbs(downstreamServerConfiguration.getEndpoint())
      .sendJsonObject(new JsonObject(convertCachedStatusesToString(statuses)))
      .onSuccess(res -> {
        if (res.statusCode() == 200 || res.statusCode() == 201) {
          LOGGER.info("Http request to {} send successfully", downstreamServerConfiguration.getEndpoint());
        } else {
          LOGGER.warn("Something went wrong. Reason: {}", res.statusMessage());
        }
      })
    .onFailure(handler -> {
      LOGGER.warn("Could not send updated statuses. Reason: {}", handler.getMessage());
    });
  }

  private void sendMessageToStoreStatuses(JsonObject response, Message<Object> message) {
    LOGGER.info("Sending message to Cache verticle to store received EIPA DATA");
    vertx.eventBus()
      .request(CACHE_VERTICLE_ADDRESS, response, prepareDeliveryOptions(CACHE_VERTICLE_SAVE), ar -> {
        if (ar.succeeded()) {
          message.reply("OK");
        }
      });
  }

  private void readConfig(JsonObject config) {
    try {
      downstreamServerConfiguration = mapper.readValue(config.getString("downstream.server"), DownstreamServerConfiguration.class);
      EIPAServerConfiguration = mapper.readValue(config.getString("eipa.server"), EIPAServerConfiguration.class);
      checkConfigurationFields();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Could not read configuration.");
    }
  }

  private void checkConfigurationFields() {
    if (downstreamServerConfiguration.checkEmptyFields()) {
      LOGGER.error("Downstream server configuration error: endpoint field is null or empty. Closing app...");
      vertx.close();
    }
    if (EIPAServerConfiguration.checkEmptyFields()) {
      LOGGER.error("EIPA server configuration error: endpoint or api key field is null or empty. Closing app...");
      vertx.close();
    }
  }

  private static Map<Integer, Status> convertJsonToStatuses(String json) {
    try {
      TypeReference<HashMap<Integer, Status>> typeRef = new TypeReference<>() {
      };
      return mapper.readValue(json, typeRef);
    } catch (JsonProcessingException e) {
      LOGGER.error("Could not deserialize json into set of statuses. Returning empty set.");
      return Collections.emptyMap();
    }
  }

  private String convertCachedStatusesToString(Map<Integer, Status> statuses) {
    try {
      List<UpdatedStatus> updatedStatuses = statuses.entrySet().stream()
        .map(entry -> new UpdatedStatus(entry.getKey(), entry.getValue().getStatus(), entry.getValue().getTs()))
        .collect(Collectors.toList());
      return mapper.writeValueAsString(new EIPAUpdatedStatus(updatedStatuses));
    } catch (JsonProcessingException e) {
      LOGGER.error("Could not serialize updated statuses object into String. Returning empty string.");
      return "";
    }
  }
}
