package edu.stanford.nlp.ie.persian;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

  public static void addRecommendations(List<List<CoreLabel>> input) throws IOException {
    final List<String> detectedNames = new ArrayList<>();
    for (List<CoreLabel> sentence : input) {
      for (CoreLabel token : sentence) {
        final String word = token.word();
        final String ner = token.get(CoreAnnotations.AnswerAnnotation.class);
        if (detectedNames.contains(word))
          token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "REC");
        if (ner.contains("PERS")) {
          detectedNames.add(word);
          if (detectedNames.size() > 10) detectedNames.remove(0);
        }
        token.remove(CoreAnnotations.AnswerAnnotation.class);
      }
    }
  }
}
