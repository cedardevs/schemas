package org.cedar.schemas.avro.psi

import spock.lang.Specification


class DefaultValuesSpec extends Specification {

  def "default analysis has nulls"() {
    def value = Analysis.newBuilder().build()

    expect:
    value.titles == null
    value.identification == null
    value.temporalBounding == null
    value.dataAccess == null
    value.description == null
    value.spatialBounding == null
    value.thumbnail == null
  }

}
