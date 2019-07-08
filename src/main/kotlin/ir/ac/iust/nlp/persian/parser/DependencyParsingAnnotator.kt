package ir.ac.iust.nlp.persian.parser

import edu.stanford.nlp.ling.TaggedWord
import ir.ac.iust.nlp.jhazm.DependencyParser
import ir.ac.iust.nlp.jhazm.POSTagger
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun main(args: Array<String>) {
  if (args.size != 2) {
    println("Please provide all parameters: model_file file/folder")
    System.exit(0)
  }

  val path = Paths.get(args[0])
  if(Files.isDirectory(path)) {
    PathWalker.getPath(path, ".*.txt").forEach { p ->
      addToFile(p)
    }
  } else {
    addToFile(path)
  }
}

private fun addToFile(path: Path) {
  val fileContent = Files.readAllLines(path)
  val lastSentence = mutableListOf<NerTaggedWord>()
  val splittingRegex = Regex("\t+")
  val buffer = StringBuilder()
  val columns = fileContent.first().split(splittingRegex).size
  fileContent.forEachIndexed { index, line ->
    println("processing line number $index")
    if (line.isNotBlank()) {
      val parts = line.split(splittingRegex)
      if (parts.size != columns) {
        println("Wrong line detected. exiting ...")
        println(line)
        System.exit(1)
      }
      when {
        parts.size == 3 -> {
          val taggedWord = NerTaggedWord(parts[2])
          taggedWord.taggedWord.setWord(parts[0])
          taggedWord.taggedWord.setTag(parts[1])
          lastSentence.add(taggedWord)
        }
        parts.size == 2 -> {
          val taggedWord = NerTaggedWord(parts[1])
          taggedWord.taggedWord.setWord(parts[0])
          lastSentence.add(taggedWord)
        }
        parts.size == 1 -> {
          val taggedWord = NerTaggedWord()
          taggedWord.taggedWord.setWord(parts[0])
          lastSentence.add(taggedWord)
        }
      }
    }
    if (line.isBlank() && lastSentence.isNotEmpty())
      addSentence(buffer, lastSentence)
  }
  if (lastSentence.isNotEmpty())
    addSentence(buffer, lastSentence)

  Files.write(Paths.get(path.fileName.toString() + ".dep"), buffer.toString().toByteArray(Charsets.UTF_8))
}

fun addSentence(buffer: StringBuilder, lastSentence: MutableList<NerTaggedWord>) {

  if (lastSentence.first().taggedWord.tag() == null) {
    val tagged: MutableList<TaggedWord> = POSTagger.i().batchTag(lastSentence.map { it.taggedWord.word() })
    if (tagged.size != lastSentence.size) {
      println(lastSentence)
      System.exit(1)
    } else {
      for (i in 0 until lastSentence.size)
        lastSentence[i].taggedWord.setTag(tagged[i].tag())
    }
  }

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

      if (token.ner != null) buffer.append('\t').append(token.ner) else buffer.append('\n')
    }
    buffer.append('\n')
  } catch (th: Throwable) {
    System.err.println("error in parsing $sentence")
  }
  lastSentence.clear()
}