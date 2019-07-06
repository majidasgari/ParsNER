package ir.ac.iust.nlp.persian.nsurl.convert

import ir.ac.iust.nlp.persian.parser.PathWalker
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes

fun main() {
  val pathes = PathWalker.getPath(Paths.get("300K"))
  pathes.addAll(PathWalker.getPath(Paths.get("600K")))
//    pathes.sortBy { Files.readAttributes(it, BasicFileAttributes::class.java).creationTime() }
  val results = mutableListOf<String>()
  pathes.forEach { path ->
    val lines = Files.readAllLines(path, charset("UTF-8"))
    if (lines.isNotEmpty()) {
      lines.forEach { line2 ->
        val line = line2.trim().replace(' ', '_')
        if (line == "." || line == ".\tO") {
          if (line == ".")
            results.add(".\tO")
          else
            results.add(line)
          results.add("")
        } else {
          val splits = line.split(Regex("\t"))
          if (line.isNotBlank() && splits.size != 2) {
            if (splits.size == 1 && line.endsWith("O")) {
              if (line == "O")
                results.add("-\tO")
              else
                results.add(line.substringBeforeLast("O") + "\tO")
              println(results.last())
            } else {
              results.add("$line\tO")
              println(results.last())
            }
          } else {
            results.add(line)
          }
        }
      }
      results.add("")
    }
  }
  Files.write(Paths.get("train.conll"), results, charset("UTF-8"))
}