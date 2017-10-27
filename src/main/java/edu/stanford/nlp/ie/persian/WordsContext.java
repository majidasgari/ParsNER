package edu.stanford.nlp.ie.persian;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WordsContext implements Serializable {
  private List<SentenceGram> sentences = new ArrayList<>();

  public List<SentenceGram> getSentences() {
    return sentences;
  }

  public void setSentences(List<SentenceGram> sentences) {
    this.sentences = sentences;
  }
}
