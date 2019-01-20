package ir.ac.iust.nlp.persian.parser

import ir.ac.iust.nlp.jhazm.DependencyParser
import java.lang.StringBuilder
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
  if (args.size != 1) {
    println("Please provide file name as first parameters of this application")
    System.exit(0)
  }

  val fileContent = Files.readAllLines(Paths.get(args[0]))
  val lastSentence = mutableListOf<NerTaggedWord>()
  val splittingRegex = Regex("\t+")
  val buffer = StringBuilder()
  fileContent.forEachIndexed { index, line ->
    println("processing line number $index")
    if (line.isNotBlank()) {
      val parts = line.split(splittingRegex)
      if (parts.size != 3) {
        println("Wrong line detected. exiting ...")
        System.exit(1)
      }
      val taggedWord = NerTaggedWord(parts[2])
      taggedWord.taggedWord.setWord(parts[0])
      taggedWord.taggedWord.setTag(parts[1])
      lastSentence.add(taggedWord)
    }
    if (line.isBlank() && lastSentence.isNotEmpty())
      addSentence(buffer, lastSentence)
  }
  if (lastSentence.isNotEmpty())
    addSentence(buffer, lastSentence)

  Files.write(Paths.get(args[0] + ".dep"), buffer.toString().toByteArray(Charsets.UTF_8))
}

fun addSentence(buffer: StringBuilder, lastSentence: MutableList<NerTaggedWord>) {
  val sentence = lastSentence.joinToString(" ") { it.taggedWord.word() }
  println("checking sentence $sentence")
  try {
    val parseTree = DependencyParser.i().rawParse(lastSentence.map { it.taggedWord })
    for (i in lastSentence.indices) {
      val token = lastSentence[i]
      val dependency = DependencyInformation(parseTree.getDependencyNode(i + 1))
      buffer.append(token.taggedWord.word())
              .append('\t')
              .append(token.taggedWord.tag())
              .append('\t')
              .append(dependency.relation)
              .append('\t')
              .append(dependency.head)
              .append('\t')
              .append(if (dependency.head == 0) "ROOT" else lastSentence[dependency.head - 1].taggedWord.word())
              .append('\t')
              .append(if (dependency.head == 0) "ROOT" else lastSentence[dependency.head - 1].taggedWord.tag())
              .append('\t')
              .append(token.ner)
              .append('\n')
    }
    buffer.append('\n')
  } catch (th: Throwable) {
    System.err.println("error in parsing $sentence")
  }
  lastSentence.clear()
}