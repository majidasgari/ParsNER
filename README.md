# Method

ParsNER is a system based on Stanford NER tagger. It used [JHazm](https://github.com/mojtaba-khallash/JHazm) for preprocessing tasks. 

ParsNER uses following features:
1. Standard StanfordNER Language modelling features
1. POS  Tags: Part  of  speech  tagging  features  are  vital  features  in  manymachine learning approaches.  Especially in NER, knowing the role of aword in a sentence can give us essential information to find named entities.
1. Wordlist: Most  of  the  foreign  names  in  a  language  are  composed  ofunknown words of that language.  Consequently, another useful feature to135detect especially foreign names is a standard word list of a language.
1. Postfix and Prefix: Another useful and commonly-used features in NERis  hand-crafted  words  which  can  be  used  as  prefix  or  postfix  of  namedentities.
1. Keyword  Lists: We  have  gathered  a  full  list  of  134  job  titles  in  the140Persian language.  Also, we have listed 206 currencies, 247 foods (for PROtags), 625 appliances (for PRO tag), 65 sports (for EVE tags), 69 ordinalkeywords (for EVE tags), and 13 time-ranges (for EVE tags).
1. Name Gazetteers: The Persian language borrows many person namesfrom other languages.  We can see many foreign names, including Arabic,145Turkish, Latin, and even French names in Persian culture.
1. NE Lists: We constructed five gazetteers: a list of 89810 of person names,18566 of organization names, and 317154 of location names, 45119 of Fa-cility/Location names and 4961 of Facility/Organization names.
1. Dependency Parsing: Parsing trees can add useful information to im-150prove the quality of NER. In many types of research, base phrasechunking  has  been  used  as  a  feature  for  NER.  We  have  added  four  fol-lowing  features  to  our  NER  system:   dependency  relation,  head  index,headword, and head POS tag.
1. Wikipedia Infoboxes: We have used a list of the titles of Wikipedia articles which have infoboxes.

## Precedence feature
We added a new post-tagging phase to Stanford software which does not exist in any previous NER systems. In many types of texts, especially in news texts, the writer mentions a person as an introduction and mentions to that name many times in continue of the text. For example, a writer named Barack Obama on the first line with its job title, and uses Obama in the rest of the text. For a sequence tagger, detection of a full name like “Barack Obama” as is very easier than a single word mention like “Obama”; Because we have a sequence of first name and surnames in full names. We invented the “precedent” option to handle such cases. To apply this intuition, we added the post-tag phase to the Stanford NER software, in this phase, algorithm stores all words of last N person names in a list (precedent-list) and when it encountered with an O tag in next words, replace O with PERS if the word existed in current precedent-list. 

## Social Data
In recent years, emojis have become very popular in social media. Also, hashtags and mentions have become standard in many social networks. An option has been added to ParsNER to handle these cases. These options are applied in these conditions:

* If an emoji has been placed in the beginning, ending of the middle of another word, will be replaced to empty characters.
* hash and at sign characters are removed from all hashtags and mentions.
* underline character is replaced with a space character in all mentions or hashtags.

# How to Build/RUN

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
