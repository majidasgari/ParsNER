package edu.stanford.nlp.ie.persian;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SentenceGram implements Serializable {
  private List<String> previousWords = new ArrayList<>();
  private List<String> nextWords = new ArrayList<>();

  public List<String> getPreviousWords() {
    return previousWords;
  }

  public List<String> getNextWords() {
    return nextWords;
  }
}
