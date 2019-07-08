package ir.ac.iust.nlp.persian.parser

import edu.stanford.nlp.ling.TaggedWord

data class NerTaggedWord(
        var ner: String? = null,
        var taggedWord: TaggedWord = TaggedWord()
)