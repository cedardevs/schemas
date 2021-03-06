package org.cedar.schemas.analyze;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadingLevel {
  private static final Logger log = LoggerFactory.getLogger(ReadingLevel.class);

  // source for syllables: https://codegolf.stackexchange.com/questions/47322/how-to-count-the-syllables-in-a-word
  // note this is an estimate, and occasionally gets the answer wrong, but is generally close enough
  private static Pattern syllablePattern = Pattern.compile("[aiouy]+e*|e(?!d$|ly).|[td]ed|le$");

  public static long findSyllablesInWord(String originalWord) {
    String word = originalWord.toLowerCase();
    Matcher matcher = syllablePattern.matcher(word);
    long count = 0;
    while (matcher.find()) {
      count++;
    }
    return count;
  }

  public static String[] splitIntoSentences(String text) {
    return text.split("([.?!])\\s");
  }

  public static String[] splitIntoWords(String text) {
    return text.toLowerCase().replaceAll("[^\\w\\a\\s)]", "").split("\\s");
  }

  public static int totalSentences(String text) {
    return splitIntoSentences(text).length;
  }

  public static int totalWords(String text) {
    return splitIntoWords(text).length;
  }

  public static long totalSyllables(String text) {
    return Arrays.stream(splitIntoWords(text)).mapToLong(ReadingLevel::findSyllablesInWord).sum();
  }

  /**
   * Score value should be 0-100.
   * 100: very easy to read
   *   0: readable only by college graduate (very difficult)
   */
  public static double FleschReadingEaseScore(String text) {
    double words = (double) totalWords(text);
    double sentences = (double) totalSentences(text);
    double syllables = (double) totalSyllables(text);
    return 206.835 - 1.015 * (words/sentences) - 84.6 * (syllables/words);
  }

  public static double FleschKincaidReadingGradeLevel(String text) {
    double words = (double) totalWords(text);
    double sentences = (double) totalSentences(text);
    double syllables = (double) totalSyllables(text);
    return 0.39 * (words/sentences) + 11.8 * (syllables/words) - 15.59;
  }

  public static boolean wcagReadingLevelCriteria(String text) {
    // Number score =  FleschReadingEaseScore(text)
    // return score >= 60 // 9th grade reading level or easier, I *think* this correlates to 'lower secondary education level'
    return FleschKincaidReadingGradeLevel(text) <= 9;
  }
}
