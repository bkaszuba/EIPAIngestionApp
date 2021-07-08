package com.kaszuba.eipa.EIPAIngestionApp


import com.kaszuba.eipa.EIPAIngestionApp.model.mapper.EIPAObjectMapper
import com.kaszuba.eipa.EIPAIngestionApp.model.received.EIPAReceivedData
import spock.lang.Specification

import java.nio.file.Paths

import static java.nio.file.Files.readString

class DeserializationSpec extends Specification {

  String mockDataLocation = 'src/test/resources/test.json'

  def 'should deserialize mock data properly'() {
    given: 'loaded json data into string'
        String string = readString(Paths.get(mockDataLocation))

    when: 'trying to deserialize it'
        EIPAReceivedData value = EIPAObjectMapper.getMapper().readValue(string, EIPAReceivedData.class)

    then: 'deserialized successfully'
        assert value.points.size() == 2
  }
}
