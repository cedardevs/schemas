package org.cedar.schemas.analyze;

import org.apache.commons.text.similarity.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GCMDKeywordScraper {

  private static final Logger log = LoggerFactory.getLogger(GCMDKeywordScraper.class);

  public static final String GCMD_DIRECTORY_URL = "https://gcmdservices.gsfc.nasa.gov/static/kms/";
  public static final String GCMD_CONCEPT_SCHEMES_URL = "https://gcmdservices.gsfc.nasa.gov/kms/concept_schemes/";



  public static String getSiteHtmlAsString(String url) {
    try {
      Document doc = Jsoup.connect(url).get();
      return doc.toString();
    }
    catch(IOException e) {
      log.error("Could not connect to the URL [ " + url + " ]");
      return "";
    }
  }

  public static String getSiteHtmlAsCleanedString(String htmlText) {
    return Jsoup.clean(htmlText, Whitelist.basicWithImages());
  }

  public static Map scrapeCategoriesAndURLsFromHtml(String htmlText) {
    /*
    GET the "Concept Schemes" aka categories:
    https://gcmdservices.gsfc.nasa.gov/kms/concept_schemes/
    Note that this ONLY returns XML...

    Using the "name" attribute for each "Scheme" element, GET keywords via:
    https://gcmdservices.gsfc.nasa.gov/kms/concepts/concept_scheme/{name}?format=csv

    UNFORTUNATELY, not every "concept scheme" is actually a GCMD keyword category --
    https://gcmdservices.gsfc.nasa.gov/static/kms/

    In order to minimize computation over excess "concept schemes", we'll scrape the HTML from the above page to get
    the actual URLs we care about.

    This isn't great at all, but reduces the wasted resources later when running comparisons between incoming strings
    and known keywords.
     */
    Map<String, String> gcmdCategories = new HashMap<>();
    try {
      Document doc = Jsoup.parse(htmlText);

      // Retrieve categories
      var body = doc.body();
      var categoryBody = body.getElementsByTag("ul").first();
      var categories = categoryBody.getElementsByTag("li").eachText();
      var csvLinks = categoryBody.select("a[href*=format=csv]").eachAttr("href");

      if(categories.isEmpty() || csvLinks.isEmpty()) {
        throw new Exception("Found zero categories or links.");
      }

      if(categories.size() != csvLinks.size()) {
        throw new Exception("Mismatch between category count and link count.");
      }

      for(int i = 0; i < categories.size(); i++) {
        gcmdCategories.put(categories.get(i).strip(), csvLinks.get(i).strip());
      }
    }
    catch (Exception e) {
      log.error("Could not parse the GCMD Static Directory page. Error details: " + e.getMessage());
    }

    return gcmdCategories; // FIXME -- throwing errors & logging while returning empty map is weird
  }

  public static Map updateCategoryMapWithLongNames(Map<String, String> originalMap, String htmlText) {
    /*
    Because the categories scraped from the static HTML page don't necessarily match the official long name (and may use
    unfamiliar acronyms), we'll update the category names with the official 'longName' using the 'name' attribute harvested
    from the links scraped from the static HTML site (deep breath!!!) that all have the form:

    https://gcmdservices.gsfc.nasa.gov/kms/concepts/concept_scheme/{name}?format=csv

    Also, because it works, treating the XML response like HTML here. Awesome.
     */
    Map<String, String> cleanMap = new HashMap<>();

    Map<String, String> namesMap = new HashMap<>();
    Document doc = Jsoup.parse(htmlText);
    var schemes = doc.getElementsByTag("scheme");
    schemes.forEach( scheme -> {
      namesMap.put(scheme.attr("name").strip(), scheme.attr("longName").strip());
    });

    Pattern p = Pattern.compile("^.+/(.+?)/??\\?format=csv$");
    originalMap.forEach( (scrapedName, url) -> {
      Matcher m = p.matcher(url);
      if(m.find()) {
        var name = m.group(1);
        var longName = namesMap.get(name);

        if(longName != null) {
          cleanMap.put(namesMap.get(name), url);
        }
        else {
          // FIXME -- don't know what to do here so do everything...???
          log.error("Scraped name not found from API: [ " + name + " ]");
          cleanMap.put(scrapedName, url);
        }

      }
      else {
        // FIXME -- don't know what to do here so do everything...???
        log.error("URL format unknown: [ " + url + " ]");
        cleanMap.put(scrapedName, url);
      }
    });

    return cleanMap;
  }

//  public static String titleCaseKeyword(String keyword) {
//
//  }

//  public static Map buildComparisonStrings(Map<String, String> namesAndUrls) {
//
//  }
//
//  public static Map buildMetadataStrings(Map<String, String> namesAndUrls) {
//
//  }

  public static Number compareStrings(String unknown, String known) {
    CosineDistance cosDist = new CosineDistance();
    CosineSimilarity cosSim = new CosineSimilarity();
    FuzzyScore fuzz = new FuzzyScore(Locale.ENGLISH);
    HammingDistance hamDist = new HammingDistance();
    JaroWinklerDistance jwDist = new JaroWinklerDistance();
    JaroWinklerSimilarity jwSim = new JaroWinklerSimilarity();
    LevenshteinDistance levDist = new LevenshteinDistance();
    LevenshteinDetailedDistance levDetails = new LevenshteinDetailedDistance();

    log.info("Comparing stings: [ " + unknown + " ]  vs [ " + known + " ]");

    var start = System.currentTimeMillis();
    var cosDistScore = cosDist.apply(unknown, known);
    var end = System.currentTimeMillis();
    log.info("Cosine distance score (words): " + cosDistScore + " took: " + (end - start));

    start = System.currentTimeMillis();
    Map<CharSequence, Integer> left = Arrays.stream(
        unknown.split("")).collect(Collectors.toMap(c -> c, c -> 1, Integer::sum));
    Map<CharSequence, Integer> right = Arrays.stream(
        known.split("")).collect(Collectors.toMap(c -> c, c -> 1, Integer::sum));
    var cosSimScore = cosSim.cosineSimilarity(left, right);
    end = System.currentTimeMillis();
    log.info("Cosine similarity score (characters): " + cosSimScore + " took: " + (end - start));

    start = System.currentTimeMillis();
    var fuzzScore = fuzz.fuzzyScore(unknown, known);
    end = System.currentTimeMillis();
    log.info("Fuzzy score: " + fuzzScore + " took: " + (end - start));

    if(unknown.length() == known.length()) {
      start = System.currentTimeMillis();
      var hamScore = hamDist.apply(unknown, known);
      end = System.currentTimeMillis();
      log.info("Ham score: " + hamScore + " took: " + (end - start));
    }

    start = System.currentTimeMillis();
    var jwDistScore = jwDist.apply(unknown, known);
    end = System.currentTimeMillis();
    log.info("JW Distance score: " + jwDistScore + " took: " + (end - start));

    start = System.currentTimeMillis();
    var jwSimScore = jwSim.apply(unknown, known);
    end = System.currentTimeMillis();
    log.info("JW Similarity score: " + jwSimScore + " took: " + (end - start));

    start = System.currentTimeMillis();
    var levDistScore = levDist.apply(unknown, known);
    end = System.currentTimeMillis();
    log.info("Levenshtein score: " + levDistScore + " took: " + (end - start));

    start = System.currentTimeMillis();
    var levDetailScores = levDetails.apply(unknown, known);
    end = System.currentTimeMillis();
    log.info("Levenshtein detailed score: " + levDetailScores + " took: " + (end - start));


    return Math.random();
  }
}
