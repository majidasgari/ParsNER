package ir.ac.iust.nlp.persian.parser

import edu.stanford.nlp.ling.TaggedWord

data class NerTaggedWord(
        var ner: String,
        var taggedWord: TaggedWord = TaggedWord()
)