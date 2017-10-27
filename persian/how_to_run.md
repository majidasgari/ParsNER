# Train phase 1
``
cd persian
java -Xmx10g -cp edu.stanford.nlp.ie.crf.CRFClassifier -prop persian.properties
``

# Test phase 1
``
cd persian
java -cp edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier ner-model.ser.gz  -prop persian.properties -testFile test/test.pos
``

# Run History Feature
