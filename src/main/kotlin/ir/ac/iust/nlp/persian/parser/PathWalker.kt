/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.nlp.persian.parser

import java.nio.file.Files
import java.nio.file.Path

object PathWalker {

  // adding java friendly functions.
  fun getPath(path: Path) = getPath(path, null, null)

  fun getPath(path: Path, pattern: Regex) = getPath(path, pattern, null)
  fun getPath(path: Path, pattern: String) = getPath(path, Regex(pattern), null)
  fun getPath(path: Path, maxDepth: Int?) = getPath(path, null, maxDepth)
  fun getPath(path: Path, pattern: String, maxDepth: Int) = getPath(path, Regex(pattern), maxDepth)
  fun getPath(path: Path, pattern: Regex?, maxDepth: Int?): MutableList<Path> {
    val result = mutableListOf<Path>()
    walk(path, pattern, result, 0, maxDepth)
    return result
  }

  private fun walk(path: Path, pattern: Regex?, list: MutableList<Path>, depth: Int, maxDepth: Int?) {
    if (maxDepth != null && depth == maxDepth) return
    Files.newDirectoryStream(path).use { stream ->
      val it = stream.iterator()
      while (it.hasNext()) {
        val p = it.next()
        if (Files.isDirectory(p)) walk(p, pattern, list, depth + 1, maxDepth)
        else if (pattern == null || pattern.matches(p.fileName.toString())) list.add(p)
      }
    }
  }
}