package com.kaszuba.eipa.EIPAIngestionApp.repository


import com.kaszuba.eipa.EIPAIngestionApp.model.received.Status
import spock.lang.Specification

import java.time.ZonedDateTime

class CacheRepositorySpec extends Specification {

  CacheRepository cacheRepository = new CacheRepository()

  def 'should store statuses and get only updated ones'() {
    given: 'new statuses to be saved'
        Map<Integer, Status> statuses = Map.of(
          1, new Status(1, 1, ZonedDateTime.now()),
          2, new Status(0, 0, ZonedDateTime.now())
        )

    when: 'saving statues'
        cacheRepository.saveStatuses(statuses)

    then: 'correct cached statuses are returned'
        assert cacheRepository.getUpdatedStatuses().size() == 2

    when: 'saving the same statuses and nothing have changed'
        cacheRepository.saveStatuses(statuses)

    then: 'there is 0 new or updated statuses'
        assert cacheRepository.getUpdatedStatuses().size() == 0

    when: 'one of statuses is updated'
        Map<Integer, Status> updatedStatuses = Map.of(
          1, new Status(1, 1, ZonedDateTime.now()),
          2, new Status(1, 1, ZonedDateTime.now())
        )
        cacheRepository.saveStatuses(updatedStatuses)

    then: 'there is 1 new or updated status'
        Map<Integer, Status> cachedStatus = cacheRepository.getUpdatedStatuses()
        assert cachedStatus.size() == 1
  }
}
