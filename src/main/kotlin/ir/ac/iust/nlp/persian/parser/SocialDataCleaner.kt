package ir.ac.iust.nlp.persian.parser

import com.vdurmont.emoji.EmojiParser

object SocialDataCleaner {
  private val spaceRemoveRegex: Regex = Regex("[ \\t\\x0B\\f]+")
  private val zeroWidthJoinerRemoveRegex: Regex = Regex("[\\u200c]+")
  private val enterRemoveRegex: Regex = Regex("[\r\n]+")

  fun normalize(input: String?): String? {
    if (input == null) return null
    //removing all sequence of spaces:
    var result = input.replace(spaceRemoveRegex, " ")
    //removing all sequence of zero width joiners:
    result = result.replace(zeroWidthJoinerRemoveRegex, "\u200c")
    //convert all sequences of \r and \n characters to one \n
    result = result.replace(enterRemoveRegex, "\n")
    result = result.replace('\u064a', '\u06cc')//ya
    result = result.replace('\u0643', '\u06a9')//kaaf
    result = result.replace("\u0640", "")//persian underline character
    return result
  }

  fun removeEmojis(str: String?): String? {
    if (str == null) return null
    return EmojiParser.removeAllEmojis(str)
  }

  fun cleanWord(str: String?): String? {
    if (str == null) return null
    var result = removeEmojis(str) ?: return null
    if(result.startsWith('#') || result.startsWith('@')) result = result.substring(1)
    return result.replace('_', ' ')
  }
}