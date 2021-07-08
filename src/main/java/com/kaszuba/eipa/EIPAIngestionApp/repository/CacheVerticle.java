package com.kaszuba.eipa.EIPAIngestionApp.repository;

import static com.kaszuba.eipa.EIPAIngestionApp.model.TopicConstants.CACHE_VERTICLE_ADDRESS;
import static com.kaszuba.eipa.EIPAIngestionApp.model.TopicConstants.CACHE_VERTICLE_GET;
import static com.kaszuba.eipa.EIPAIngestionApp.model.TopicConstants.CACHE_VERTICLE_SAVE;
import static com.kaszuba.eipa.EIPAIngestionApp.model.TopicConstants.HEADER_OPERATION_NAME;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.run;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaszuba.eipa.EIPAIngestionApp.model.mapper.EIPAObjectMapper;
import com.kaszuba.eipa.EIPAIngestionApp.model.received.EIPAReceivedData;
import com.kaszuba.eipa.EIPAIngestionApp.model.received.Point;
import com.kaszuba.eipa.EIPAIngestionApp.model.received.Status;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(CacheVerticle.class);
  private static final ObjectMapper mapper = EIPAObjectMapper.getMapper();
  private CacheRepository cacheRepository;

  @Override
  public void start() {
    cacheRepository = new CacheRepository();
    vertx.eventBus().consumer(CACHE_VERTICLE_ADDRESS, this::handleMessage);
  }

  private void handleMessage(Message<Object> message) {
    String operation = message.headers().get(HEADER_OPERATION_NAME);
    LOGGER.info("Received message with operation: {}.", operation);
    Match(operation).of(
      Case($(CACHE_VERTICLE_SAVE), event -> run(() -> this.putStatuses(message))),
      Case($(CACHE_VERTICLE_GET), event -> run(() -> this.getCacheStatuses(message)))
    );
  }

  private void getCacheStatuses(Message<Object> message) {
    LOGGER.info("Received message to get updated statuses.");
    message.reply(convertStatusesToJson(cacheRepository.getUpdatedStatuses()));
  }

  private void putStatuses(Message<Object> message) {
    LOGGER.info("Received message to store statuses in cache.");
    try {
      cacheRepository.saveStatuses(convertResponseToStatuses(message.body().toString()));
      message.reply("OK");
    } catch (JsonProcessingException e) {
      LOGGER.error("Could not deserialize message with statuses. Nothing saved in cache.");
    }
  }

  private Map<Integer, Status> convertResponseToStatuses(String response) throws JsonProcessingException {
    EIPAReceivedData EIPAReceivedData = mapper.readValue(response, EIPAReceivedData.class);
    return EIPAReceivedData.getPoints().stream()
      .filter(point -> Objects.nonNull(point.getStatus()))
      .collect(Collectors.toMap(Point::getPointId, Point::getStatus));
  }

  private String convertStatusesToJson(Map<Integer, Status> statuses) {
    try {
      return mapper.writeValueAsString(statuses);
    } catch (JsonProcessingException e) {
      LOGGER.error("Could not serialize set of statuses into json. Returning empty json.");
      return "";
    }
  }
}
