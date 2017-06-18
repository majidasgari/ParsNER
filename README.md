Using Stanford NER Parser for Persian language ...

for training in IDE:

``
jvm options: -Xmx5g
program arguments: -prop persian.properties
``
or in command line:

``
java -cp stanford-ner.jar edu.stanford.nlp.ie.crf.CRFClassifier -prop persian.properties
``

for testing:

``
program arguments: -model ner-model.ser.gz -testFile test.pos
``

``
java -cp stanford-ner.jar edu.stanford.nlp.ie.crf.CRFClassifier -model ner-model.ser.gz -testFile test.pos
``