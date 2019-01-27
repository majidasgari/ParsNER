package ir.ac.iust.nlp.persian.parser

import edu.stanford.nlp.ie.persian.HistoryRecommender
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.ling.CoreLabel
import java.lang.StringBuilder
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.text.Charsets.UTF_8

fun main(args: Array<String>) {
  if (args.size != 1) {
    println("Please provide a simple CoNLL 3-column NER result (word, GoldNER, NER).")
    System.exit(0)
  }

  val inputPath = Paths.get(args[0])
  val lines = Files.readAllLines(inputPath, UTF_8)

  val outputLines = mutableListOf<String>()
  val list = mutableListOf<CoreLabel>()
  for (line in lines) {
    if(line.isNullOrBlank()) {
      if(list.isNotEmpty()) {
        add(list, outputLines)
      }
    }
    val splits = line.split(Regex("\t"))
    if (splits.size == 3) {
      val c = CoreLabel()
      c.setWord(splits[0])
      c.setTag("N")
      c.set(CoreAnnotations.GoldAnswerAnnotation::class.java, splits[1])
      c.set(CoreAnnotations.AnswerAnnotation::class.java, splits[2])
      list.add(c)
    }
  }

  add(list, outputLines)

  Files.write(inputPath.parent.resolve(inputPath.fileName.toString() + ".p2"), outputLines, UTF_8)
}

private fun add(list: MutableList<CoreLabel>, lines: MutableList<String>) {
  HistoryRecommender.manipulateNEARBasedOnPrecedence(list, 5)
  for (l in list) {
    try {
      val builder = StringBuilder()
      builder.append(l.word()).append('\t')
              .append(l.get(CoreAnnotations.AnswerAnnotation::class.java)).append('\t')
              .append(l.get(CoreAnnotations.GoldAnswerAnnotation::class.java)).append('\t')
              .append(if (l.containsKey(CoreAnnotations.UnknownAnnotation::class.java))
                l.get(CoreAnnotations.UnknownAnnotation::class.java) else "O")
      lines.add(builder.toString())
    } catch (th: Throwable) {
      println(th)
    }
  }
  list.clear()
  lines.add("")
}