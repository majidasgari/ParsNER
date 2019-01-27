package ir.ac.iust.nlp.persian.parser

import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {

  if (args.size != 1) {
    println("Please provide folder of vectors.")
    System.exit(0)
  }

  val inputFile = Paths.get(args[0])
  val lines = Files.readAllLines(inputFile)

  val firstLines = lines[0].split(Regex("\\s+"))
  val numberOfWords = firstLines[0].toInt()
  val vectorDimension = firstLines[1].toInt()

  val words = mutableListOf<String>()
  val vectors = mutableListOf<String>()
  words.add("UNKNOWN")
  words.add("PADDING")
  vectors.add((1..vectorDimension).map { "0" }.joinToString(" ") { it })
  vectors.add((1..vectorDimension).map { "0" }.joinToString(" ") { it })
  for (i in 1 until lines.size) {
    val splits = lines[i].split(Regex("\\s+")).filter { it.isNotBlank() }
    words.add(splits[0])
    vectors.add(splits.subList(1, splits.size).joinToString(" ") { it })
  }

  val indexFile = inputFile.parent.resolve(inputFile.fileName.toString() + ".index")
  Files.write(indexFile, words, Charsets.UTF_8)
  val vectorFile = inputFile.parent.resolve(inputFile.fileName.toString() + ".vector")
  Files.write(vectorFile, vectors, Charsets.UTF_8)
}