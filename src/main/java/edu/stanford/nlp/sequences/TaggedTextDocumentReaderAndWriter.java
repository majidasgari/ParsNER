package edu.stanford.nlp.sequences;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.WordToSentenceProcessor;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class provides methods for reading plain text documents and writing out
 * those documents once classified in several different formats.
 * The output formats are named: slashTags, xml, inlineXML, tsv, tabbedEntities.
 * <p>
 * <i>Implementation note:</i> see
 * itest/src/edu/stanford/nlp/ie/crf/CRFClassifierITest.java for examples and
 * test cases for the output options.
 * <p>
 * This class works over a list of anything that extends {@link CoreMap}.
 * The usual case is {@link CoreLabel}.
 *
 * @author Jenny Finkel
 * @author Christopher Manning (new output options organization)
 * @author Sonal Gupta (made the class generic)
 */
public class TaggedTextDocumentReaderAndWriter<IN extends CoreMap> implements DocumentReaderAndWriter<IN> {

    private static final long serialVersionUID = -2420554645189666606L;

    private final WordToSentenceProcessor<IN> wts =
            new WordToSentenceProcessor<>(WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS);

    /**
     * Construct a PlainTextDocumentReaderAndWriter. You should call init() after
     * using the constructor.
     */
    public TaggedTextDocumentReaderAndWriter() {
    }

    @Override
    public void init(SeqClassifierFlags flags) {
        //nothing to do
    }

    // todo: give options for document splitting. A line or the whole file or sentence splitting as now
    @Override
    public Iterator<List<IN>> getIterator(Reader r) {
        List<IN> words = new ArrayList<>();

        String line;
        try (BufferedReader reader = new BufferedReader(r)) {
            while ((line = reader.readLine()) != null ) {
                CoreLabel coreLabel = new CoreLabel();
                if(line.length() > 0) {
                    final String[] splits = line.split("\\s+");
                    coreLabel.setWord(splits[0]);
                    coreLabel.setValue(splits[0]);
                    coreLabel.setTag(splits[1]);
                    words.add((IN) coreLabel);
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }

        List<List<IN>> sentences = wts.process(words);
        String after = "";
        IN last = null;
        for (List<IN> sentence : sentences) {
            int pos = 0;
            for (IN w : sentence) {
                w.set(CoreAnnotations.PositionAnnotation.class, Integer.toString(pos));
                after = StringUtils.getNotNullString(w.get(CoreAnnotations.AfterAnnotation.class));
                w.remove(CoreAnnotations.AfterAnnotation.class);
                last = w;
            }
        }
        if (last != null) {
            last.set(CoreAnnotations.AfterAnnotation.class, after);
        }

        return sentences.iterator();
    }

    @Override
    public void printAnswers(List<IN> list, PrintWriter out) {
        for (IN wi : list) {
            out.print(StringUtils.getNotNullString(wi.get(CoreAnnotations.BeforeAnnotation.class)));
            out.print(StringUtils.getNotNullString(wi.get(CoreAnnotations.TextAnnotation.class)));
            out.print('/');
            out.print(StringUtils.getNotNullString(wi.get(CoreAnnotations.AnswerAnnotation.class)));
            out.print(StringUtils.getNotNullString(wi.get(CoreAnnotations.AfterAnnotation.class)));
        }
    }

}