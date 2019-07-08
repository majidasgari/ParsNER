package ir.ac.iust.nlp.persian.parser

import edu.stanford.nlp.ie.AbstractSequenceClassifier
import edu.stanford.nlp.ie.crf.CRFClassifier
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.ling.CoreLabel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.ArrayList

fun main(args: Array<String>) {
  if (args.size != 2) {
    println("Please provide all parameters: model_file file/folder")
    System.exit(0)
  }

  val classifier = CRFClassifier.getClassifier(args[0])
  val path = Paths.get(args[1])
  if(Files.isDirectory(path)) {
    PathWalker.getPath(path, ".*.dep").forEach { p ->
      tag(classifier, p.toAbsolutePath())
    }
  } else {
    tag(classifier, path.toAbsolutePath())
  }
}

fun tag(classifier: AbstractSequenceClassifier<CoreLabel>, path: Path) {
  val lines = Files.readAllLines(path)
  val regex = Regex("\t+")
  var position = 0
  val sentence = ArrayList<CoreLabel>()
  val result = mutableListOf<String>()
  for((index, line) in lines.withIndex()) {
    if(line.isBlank()) {
      if(sentence.isNotEmpty()) {
        val classified = classifier.classify(sentence)
        classified.forEach { coreLabel ->
          result.add(coreLabel.get(CoreAnnotations.AnswerAnnotation::class.java))
        }
        sentence.clear()
        position = 0
      }
      result.add("")
    } else {
      sentence.add(coreLabel(line.split(regex), position++))
    }
  }

  result.removeAt(result.size - 1)
  Files.write(Paths.get(path.toAbsolutePath().fileName.toString().substringBefore('.') + ".predict"), result, charset("UTF-8"))
}

fun coreLabel(splits: List<String>, position: Int): CoreLabel {
  val label = CoreLabel()
  label.set(CoreAnnotations.TextAnnotation::class.java, splits[0])
  label.set(CoreAnnotations.PartOfSpeechAnnotation::class.java, splits[1])
  label.set(CoreAnnotations.PositionAnnotation::class.java, position.toString())
  label.set(CoreAnnotations.CoNLLDepTypeAnnotation::class.java, splits[2])
  label.set(CoreAnnotations.HeadWordIndexAnnotation::class.java, splits[3].toInt())
  label.set(CoreAnnotations.HeadWordStringAnnotation::class.java, splits[4])
  label.set(CoreAnnotations.HeadPosStringAnnotation::class.java, splits[5])
  return label
}
