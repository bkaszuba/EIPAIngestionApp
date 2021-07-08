package com.kaszuba.eipa.EIPAIngestionApp.repository;

import com.kaszuba.eipa.EIPAIngestionApp.model.received.Status;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheRepository implements Repository {

  private static final Logger LOGGER = LoggerFactory.getLogger(CacheRepository.class);
  public Map<Integer, Status> pointsStatusCache;
  public Map<Integer, Status> updatedPoints;

  public CacheRepository() {
    this.pointsStatusCache = new HashMap<>();
  }

  public void saveStatuses(Map<Integer, Status> receivedStatuses) {
    updatedPoints = new HashMap<>();
    receivedStatuses.forEach((key, value) -> {
      if ((!pointsStatusCache.containsKey(key)) || (pointsStatusCache.containsKey(key) && !pointsStatusCache.get(key).equals(value))) {
        updatedPoints.put(key, value);
      }
      pointsStatusCache.put(key, value);
    });
    LOGGER.info("{} statuses has changed or new added since last query.", updatedPoints.size());
  }

  public Map<Integer, Status> getUpdatedStatuses() {
    LOGGER.info("Returning {} changed or new statuses", updatedPoints.size());
    return updatedPoints;
  }
}
