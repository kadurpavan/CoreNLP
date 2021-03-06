package edu.stanford.nlp.ie;


import edu.stanford.nlp.ling.SentenceUtils;
import junit.framework.TestCase;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.PaddedList;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;


/**
 * Tests that various features options produce the expected sets of strings.
 * Actually, right now it only tests the sloppy gazette.
 *
 * @author John Bauer
 */
public class NERFeatureFactoryITest extends TestCase {

  private static void checkFeatures(Set<String> features, String ... expected) {
    assertEquals(expected.length, features.size());
    for (String feature : expected) {
      assertTrue(features.contains(feature));
    }
  }

  public void testSloppyGazette() {
    List<CoreLabel> sentence = SentenceUtils.toCoreLabelList("For three years , John Bauer has worked at Stanford .".split(" +"));
    PaddedList<CoreLabel> paddedSentence = new PaddedList<>(sentence, new CoreLabel());

    Properties props = new Properties();
    props.setProperty("useGazettes", "true");
    props.setProperty("sloppyGazette", "true");
    props.setProperty("gazette", "projects/core/data/edu/stanford/nlp/ie/test_gazette.txt");
    SeqClassifierFlags flags = new SeqClassifierFlags(props);
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    Set<String> features = new HashSet<String>();
    NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(features);
    factory.featuresC(paddedSentence, 4, collector);
    checkFeatures(features, "FOO-GAZ|C", "BAR-GAZ|C", "John-WORD|C", "FOO-GAZ1|C", "BAR-GAZ2|C",
                  "BAZ-GAZ2|C", "BAZ-GAZ|C");
    features.clear();
    factory.featuresC(paddedSentence, 5, collector);
    checkFeatures(features, "BAR-GAZ|C", "BAZ-GAZ|C", "BAR-GAZ2|C", "BAZ-GAZ2|C", "Bauer-WORD|C");
    features.clear();
    factory.featuresC(paddedSentence, 6, collector);
    checkFeatures(features, "has-WORD|C");
    features.clear();
  }

}
