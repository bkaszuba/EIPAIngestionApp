package com.kaszuba.eipa.EIPAIngestionApp.repository;

import com.kaszuba.eipa.EIPAIngestionApp.model.received.Status;
import java.util.Map;

public interface Repository {

  void saveStatuses(Map<Integer, Status> receivedStatuses);

  Map<Integer, Status> getUpdatedStatuses();
}
