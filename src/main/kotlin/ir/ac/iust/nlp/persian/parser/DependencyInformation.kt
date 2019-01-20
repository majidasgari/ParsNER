package ir.ac.iust.nlp.persian.parser

import org.maltparser.concurrent.graph.ConcurrentDependencyNode

class DependencyInformation(node: ConcurrentDependencyNode? = null) {

  var position: Int = 0
  var lemma: String? = null
  var features: String? = null
  var head: Int = 0
  var relation: String? = null
  var cPOS: String? = null

  init {
    if(node != null) {
      this.cPOS = node.getLabel("CPOSTAG")
      this.features = node.getLabel("FEATS")
      val headIdLabel = node.head.getLabel("ID")
      this.head = if (headIdLabel.isEmpty()) 0 else Integer.parseInt(headIdLabel)
      this.lemma = node.getLabel("LEMMA")
      this.position = Integer.parseInt(node.getLabel("ID"))
      this.relation = node.getLabel("DEPREL")
    }
  }

  fun copy(): DependencyInformation {
    val copy = DependencyInformation()
    copy.position = this.position
    copy.lemma = this.lemma
    copy.features = this.features
    copy.head = this.head
    copy.relation = this.relation
    copy.cPOS = this.cPOS
    return copy
  }
}