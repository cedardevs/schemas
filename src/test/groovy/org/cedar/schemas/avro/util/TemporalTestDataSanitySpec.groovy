package org.cedar.schemas.avro.util

import org.cedar.schemas.avro.psi.TemporalBounding
import org.cedar.schemas.avro.psi.TemporalBoundingAnalysis
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class TemporalTestDataSanitySpec extends Specification {

  def 'the #situation test data is instantiated'() {
    expect:
    situation.description instanceof String
    situation.bounding instanceof TemporalBounding
    situation.analysis instanceof TemporalBoundingAnalysis

    where:
    situation << TemporalTestData.situtations.values()
  }

}
