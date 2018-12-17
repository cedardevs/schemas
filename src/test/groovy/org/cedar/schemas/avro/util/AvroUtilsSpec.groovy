package org.cedar.schemas.avro.util

import groovy.json.JsonSlurper
import org.cedar.schemas.avro.psi.Input
import org.cedar.schemas.avro.psi.Method
import org.cedar.schemas.avro.psi.ParsedRecord
import org.cedar.schemas.avro.psi.Publishing
import org.cedar.schemas.avro.psi.RecordType
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class AvroUtilsSpec extends Specification {

  def 'transforms an Input into a map'() {
    def builder = Input.newBuilder()
    builder.type = RecordType.granule
    builder.method = Method.POST
    builder.source = 'test'
    builder.contentType = 'application/json'
    builder.content = '{"hello":"world"}'
    def testInput = builder.build()

    expect:
    AvroUtils.avroToMap(testInput) == [
        type: RecordType.granule,
        method: Method.POST,
        source: 'test',
        contentType: 'application/json',
        content: '{"hello":"world"}'
    ]
  }

  def 'ParsedRecord has empty array of errors by default'() {
    def record = ParsedRecord.newBuilder().setType(RecordType.collection).build()

    expect:
    record.errors instanceof List
    record.errors.size() == 0
  }

  def 'produces json from avro objects'() {
    def record = Publishing.newBuilder().build()

    when:
    def json = AvroUtils.avroToJson(record)

    then:
    json instanceof String

    when:
    def parsed = new JsonSlurper().parseText(json)

    then:
    noExceptionThrown()
    parsed instanceof Map
  }

  def 'builds avro objects from json #inputType'() {
    def json = '{"isPrivate":true,"until":null}'
    def input = inputType == 'stream' ? new ByteArrayInputStream(json.bytes) : json

    when:
    def record = AvroUtils.<Publishing>jsonToAvro(input, Publishing.classSchema)

    then:
    noExceptionThrown()
    record instanceof Publishing
    record.isPrivate
    record.until == null

    where:
    inputType << ['string', 'stream']
  }

}
