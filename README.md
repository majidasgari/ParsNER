Using Stanford NER Parser for Persian language ...

for training in IDE:

```
class: edu.stanford.nlp.ie.crf.CRFClassifier
jvm options: -Xmx5g
program arguments: -prop persian.properties
```
or in command line:

```
java -cp stanford-ner.jar edu.stanford.nlp.ie.crf.CRFClassifier -prop persian.properties
```

maven:
```bash
mvn exec:java -Dexec.workingdir="persian" -Dexec.mainClass=edu.stanford.nlp.ie.crf.CRFClassifier -Dexec.args="-prop persian/persian.properties"
```

for testing:

```
class: edu.stanford.nlp.ie.crf.CRFClassifier
program arguments: -model ner-model.ser.gz -testFile test.pos
```

```
java -cp stanford-ner.jar edu.stanford.nlp.ie.crf.CRFClassifier -model ner-model.ser.gz -testFile test.pos
```

maven:
```bash
mvn exec:java -Dexec.workingdir="persian" -Dexec.mainClass=edu.stanford.nlp.ie.crf.CRFClassifier -Dexec.args="-model ner-model.ser.gz -testFile test.pos"
```

To generate .ph2 files, use

```
class edu.stanford.nlp.ie.persian.HistoryRecommender
train_news.pos test_news.pos train_wiki.pos test_wiki.pos train.pos test.pos
```

```
java -cp stanford-ner.jar edu.stanford.nlp.ie.persian.HistoryRecommender train_news.pos test_news.pos ...
```

# Run in background (Linux)
```bash
cd persian
nohup java -Xmx20g -cp ../target/stanford-persian-ner-3.9.0.jar edu.stanford.nlp.ie.crf.CRFClassifier -prop persian.properties &
```

then press `ctrl+z`.

and run `disown` command.