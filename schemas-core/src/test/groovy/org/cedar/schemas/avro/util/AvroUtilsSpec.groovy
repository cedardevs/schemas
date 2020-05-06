package org.cedar.schemas.avro.util

import groovy.json.JsonSlurper
import org.cedar.schemas.avro.psi.Checksum
import org.cedar.schemas.avro.psi.ChecksumAlgorithm
import org.cedar.schemas.avro.psi.Discovery
import org.cedar.schemas.avro.psi.FileInformation
import org.cedar.schemas.avro.psi.FileLocation
import org.cedar.schemas.avro.psi.Input
import org.cedar.schemas.avro.psi.Method
import org.cedar.schemas.avro.psi.OperationType
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
    builder.operation = OperationType.NO_OP
    def testInput = builder.build()

    when:
    def result = AvroUtils.avroToMap(testInput)

    then:
    result instanceof Map
    result.type == RecordType.granule
    result.content == '{"hello":"world"}'
    result.contentType == 'application/json'
    result.method == Method.POST
    result.source == 'test'
    result.operation == OperationType.NO_OP
  }

  def 'transforms an avro object with a nested map which contains avro values ... into a map'() {
    def builder = ParsedRecord.newBuilder()
    builder.fileLocations = [ // <-- fileLocations is a map with values which are avro objects
        'testURI': FileLocation.newBuilder().setUri('testURI').build()
    ]
    def testInput = builder.build()

    when:
    def result = AvroUtils.avroToMap(testInput)

    then:
    result instanceof Map
    result.fileLocations instanceof Map
    result.fileLocations.testURI instanceof Map
    result.fileLocations.testURI.uri == 'testURI'
  }

  def 'avroToMap handles nulls'() {
    expect:
    AvroUtils.avroToMap(null) == null
    AvroUtils.avroToMap(null, false) == null
  }

  def 'transforms a map into an input'() {
    def inputMap = [
        type       : RecordType.granule,
        method     : Method.POST,
        source     : 'test',
        contentType: 'application/json',
        content    : '{"hello":"world"}',
        operation  : OperationType.NO_OP
    ]

    when:
    def result = AvroUtils.mapToAvro(inputMap, Input)

    then:
    result instanceof Input
    result.source == 'test'
    result.contentType == 'application/json'
    result.content == '{"hello":"world"}'
  }

  def 'transforms a sparse map into a Discovery'() {
    def input = [
        fileIdentifier: 'test_id'
    ]

    when:
    def result = AvroUtils.mapToAvro(input, Discovery)

    then:
    noExceptionThrown()
    result instanceof Discovery
    result.fileIdentifier == 'test_id'
  }

  def 'mapToAvro handles nulls'() {
    expect:
    AvroUtils.mapToAvro(null, FileInformation) == null
  }

  def 'transforms a map into a FileInformation'() {
    def map = [
        name: 'testfile',
        size: 42,
        format     : null,
        //headers: null <-- intentionally excluded
        checksums: [
            [algorithm: 'MD5', value: 'abc'],
            [algorithm: 'SHA1', value: 'xyz']
        ],
        optionalAttributes: [
            "hello": "world",
            "answer": "42"
        ]
    ]

    when:
    def result = AvroUtils.mapToAvro(map, FileInformation)

    then:
    result instanceof FileInformation
    result.name == map.name
    result.size == map.size
    result.format == null
    result.headers == null // <-- should have default value set
    result.checksums instanceof List
    result.checksums.size() == 2
    result.checksums.every { it instanceof Checksum }
    result.checksums[0] == Checksum.newBuilder().setAlgorithm(ChecksumAlgorithm.MD5).setValue('abc').build()
    result.checksums[1] == Checksum.newBuilder().setAlgorithm(ChecksumAlgorithm.SHA1).setValue('xyz').build()
    result.optionalAttributes instanceof Map
    result.optionalAttributes.size() == 2
    result.optionalAttributes['hello'] == map.optionalAttributes.hello
    result.optionalAttributes['answer'] == map.optionalAttributes.answer
  }

  def 'leniently parses json into a FileInformation'() {
    def json = '{"name":"testfile"}'

    when:
    def result = AvroUtils.jsonToAvroLenient(json, FileInformation.classSchema)

    then:
    result instanceof FileInformation
    result.name == 'testfile'
    // the rest should use their default values
    result.size == 0
    result.format == null
    result.headers == null
    result.checksums instanceof List
    result.checksums.size() == 0
    result.optionalAttributes instanceof Map
    result.optionalAttributes.size() == 0
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
    when:
    def record = AvroUtils.<Publishing> jsonToAvro(input, Publishing.classSchema)

    then:
    noExceptionThrown()
    record instanceof Publishing
    record.isPrivate
    record.until == null

    where:
    inputType | input
    'string'  | '{"isPrivate":true,"until":null}'
    'stream'  | new ByteArrayInputStream('{"isPrivate":true,"until":null}'.bytes)
  }

  def 'reads the example record from json'() {
    when:
    def inputStream = ClassLoader.systemClassLoader.getResourceAsStream('example-record-avro.json')
    def inputRecord = AvroUtils.<ParsedRecord> jsonToAvro(inputStream, ParsedRecord.classSchema)

    then:
    inputRecord instanceof ParsedRecord
  }

}
