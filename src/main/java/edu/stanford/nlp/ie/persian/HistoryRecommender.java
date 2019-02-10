package edu.stanford.nlp.ie.persian;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maJid~ASGARI on 18/04/2016.
 */
public class HistoryRecommender {
  public static void main(String[] args) throws IOException {
    for (String arg : args) {
      final Path input = Paths.get(arg);
      final Path output = Paths.get(arg + ".ph2");
      addRecommendations(input, output);
    }
  }

  public static void addRecommendations(Path input, Path output) throws IOException {
    final List<String> detectedNames = new ArrayList<>();
    final List<String> lines = Files.readAllLines(input);
    List<String> builder = new ArrayList<>();
    for (String line : lines) {
      final String[] tokens = line.split("\\t");
      if (tokens.length != 3) {
        builder.add(line);
        continue;
      }
      if (detectedNames.contains(tokens[0]))
        builder.add(tokens[0] + "\tREC\t" + tokens[2]);
      else builder.add(line);

      if (tokens[2].contains("PERS")) {
        detectedNames.add(tokens[0]);
        if (detectedNames.size() > 10) detectedNames.remove(0);
      }
    }
    Files.write(output, builder);
  }

  private final static Map<String, List<String>> detectedNames = new HashMap<>();

  public static void addRecommendations(List<List<CoreLabel>> input) throws IOException {
    detectedNames.clear();
    for (List<CoreLabel> sentence : input) {
      addRecommendationsToDoc(sentence);
    }
  }

  private static void addRecommendationsToDoc(List<CoreLabel> sentence) {
    detectedNames.putIfAbsent("PERS", new ArrayList<>());
    for (CoreLabel token : sentence) {
      final String word = token.word();
      final String ner = token.get(CoreAnnotations.AnswerAnnotation.class);
      if (detectedNames.get("PERS").contains(word))
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "REC");
      if (ner.contains("PERS")) {
        detectedNames.get("PERS").add(word);
        if (detectedNames.get("PERS").size() > 10) detectedNames.get("PERS").remove(0);
      }
      token.remove(CoreAnnotations.AnswerAnnotation.class);
    }
  }

  public static void manipulateNEARBasedOnPrecedence(List sentence) {
    manipulateNEARBasedOnPrecedence(sentence, 5);
  }

  public static void manipulateNEARBasedOnPrecedence(List sentence, int precedentSize) {
    final String[] recommendedTags = new String[]{"PERS"};
    for (String recommendedTag : recommendedTags) {
      detectedNames.putIfAbsent(recommendedTag, new ArrayList<>());
      for (int i = 0; i < sentence.size(); i++) {
        Object t = sentence.get(i);
        if (!(t instanceof CoreLabel)) continue;
        CoreLabel token = (CoreLabel) t;
        final String word = token.word();
        final String tag = token.tag();
        final String ner = token.get(CoreAnnotations.AnswerAnnotation.class);
        if (ner.equals("O")) {
          if (!tag.equals("V") && !tag.equals("PUNC") && !tag.equals("CONJ") &&
                  detectedNames.get(recommendedTag).contains(word)) {
            String iob = "B-";
            if (i > 0 && (sentence.get(i - 1) instanceof CoreLabel) &&
                    ((CoreLabel) sentence.get(i - 1)).get(CoreAnnotations.AnswerAnnotation.class).contains(recommendedTag))
              iob = "I-";

            token.remove(CoreAnnotations.AnswerAnnotation.class);
            token.set(CoreAnnotations.AnswerAnnotation.class, iob + recommendedTag);
            token.set(CoreAnnotations.UnknownAnnotation.class, "X");
          }
        } else if (ner.toUpperCase().contains(recommendedTag) && (!tag.equals("CONJ") && !tag.equals("PUNC"))) {
          detectedNames.get(recommendedTag).add(word);
          while (detectedNames.get(recommendedTag).size() > precedentSize)
            detectedNames.get(recommendedTag).remove(0);
        }
      }
    }
  }
}
