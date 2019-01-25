package ir.ac.iust.nlp.persian.parser

import java.lang.StringBuilder
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.text.Charsets.UTF_8

class EmbeddingFormatConverter {
}

fun main(args: Array<String>) {

  if (args.size != 1) {
    println("Please provide folder of vectors.")
    System.exit(0)
  }

  val sourceFolder = Paths.get(args[0])
  val files = PathWalker.getPath(sourceFolder)
  files.sortBy { it.fileName.toString().toInt()}

  val builder = StringBuilder()
  var size = -1
  files.forEach { file ->
    val lines = Files.readAllLines(file)
    lines.forEach { line ->
      val splits = line.split(Regex("\\s+"))
      if(size == -1) {
        size = splits.size
        builder.append(size).append('\n')
      } else {
        if(splits.size != size) System.exit(1)
        splits.forEach { builder.append(it).append(' ') }
        builder.setLength(builder.length - 1)
        builder.append('\n')
      }
    }
  }

  for(i in 1 .. size) builder.append(0).append(' ')
  builder.setLength(builder.length - 1)
  builder.append('\n')
  for(i in 1 .. size) builder.append(0).append(' ')
  builder.setLength(builder.length - 1)
  builder.append('\n')

  val outputFile = sourceFolder.parent.resolve(sourceFolder.fileName.toString() + ".vec")
  Files.write(outputFile, builder.toString().toByteArray(UTF_8))
}