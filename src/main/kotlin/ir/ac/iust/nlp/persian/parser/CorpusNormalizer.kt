package ir.ac.iust.nlp.persian.parser

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

fun main() {

  val prefixes = mutableMapOf(
          "pers" to mutableListOf(loadAsSet("prefixes.txt"), loadAsSet("jobs.txt")),
          "fac" to mutableListOf(loadAsSet("facility_prefix.txt")),
          "pro" to mutableListOf(loadAsSet("product_keywords.txt")),
          "event" to mutableListOf(loadAsSet("event_keywords.txt")),
          "org" to mutableListOf(loadAsSet("organization_prefixes.txt")),
          "loc" to mutableListOf(loadAsSet("location_prefixes.txt"))

  )

  val inputFile = Paths.get("C:\\Users\\ASUS\\Desktop\\Arman\\result_fold3.txt")
  val outputFile = Paths.get(inputFile.toAbsolutePath().toString() + ".cor")
  val splittedLines = Files.readAllLines(inputFile, Charset.forName("UTF-8")).map { it.split("\t").toMutableList() }

  splittedLines.forEachIndexed { index, splits ->
    if (index == splittedLines.size - 1) return@forEachIndexed
    if (splits.size < 2) return@forEachIndexed
    if (splits[1] == "O" && splittedLines[index + 1].size >= 2) {
      val nextTag = splittedLines[index + 1][1]
      if (nextTag != "O")
        prefixes.forEach { tag, keywords ->
          if (nextTag == "B-$tag") {
            keywords.forEach { keyword ->
              if (keyword.contains(splits[0])) {
                splits[1] = "B-$tag"
                splittedLines[index + 1][1] = "I-$tag"
              }
            }
          }
        }
    }
  }

  Files.write(outputFile, splittedLines.map { if(it.size < 2) it[0] else it.subList(0, 2).joinToString("\t") })
}

fun loadAsSet(name: String): Set<String> {
  var lines: List<String> = mutableListOf()
  DependencyInformation::class.java.classLoader.getResourceAsStream(name).use {
    val out = Paths.get(name)
    if (!Files.exists(out)) Files.copy(it, out)
    lines = Files.readAllLines(out)
  }
  return lines.toSet()
}