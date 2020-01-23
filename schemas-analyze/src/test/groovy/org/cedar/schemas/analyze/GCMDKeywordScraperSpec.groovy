package org.cedar.schemas.analyze

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class GCMDKeywordScraperSpec extends Specification {

  def 'TEST get keywords'() {
    when:
    def map = GCMDKeywordScraper.scrapeCategoriesAndURLsFromHtml(GCMDKeywordScraper.getSiteHtmlAsString(GCMDKeywordScraper.GCMD_DIRECTORY_URL))

    then:
    println(map)
    1 == 1
  }

  def 'TEST scraping XML as HTML?'() {
    when:
    def html = GCMDKeywordScraper.getSiteHtmlAsString(GCMDKeywordScraper.GCMD_CONCEPT_SCHEMES_URL)
    def map = GCMDKeywordScraper.updateCategoryMapWithLongNames(GCMDKeywordScraper.scrapeCategoriesAndURLsFromHtml(GCMDKeywordScraper.getSiteHtmlAsString(GCMDKeywordScraper.GCMD_DIRECTORY_URL)), html)

    then:
    println(map)
    1 == 1
  }

  def 'test compareStrings'() {
    given:
    def unknown = 'hello'
    def known = 'hello hello hello'

    when:
    GCMDKeywordScraper.compareStrings(unknown, known)

    then:
    1 == 1
  }
}
