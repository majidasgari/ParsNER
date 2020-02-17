package ir.ac.iust.fixer;

import ir.ac.iust.nlp.jhazm.Lemmatizer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LemmaAdder {
    public static void main(String[] args) throws IOException {
        final Path inputPath = Paths.get(args[0]);
        final Path outputPath = Paths.get(args[1]);
        final List<String> lines = Files.readAllLines(inputPath, StandardCharsets.UTF_8);
        final List<String> outputLines = new ArrayList<>();
        for (final String line : lines) {
            if (line.startsWith("-DOCSTART-"))
                outputLines.add("-DOCSTART-\t-docstart-\t-docstart-\tN\tO");
            else if (line.trim().isEmpty())
                outputLines.add(line);
            final String[] splits = line.split("\t");
            if (splits.length == 3) {
                outputLines.add(splits[0] + "\t" + Lemmatizer.i().lemmatize(splits[0], splits[1]) +
                        "\t" + splits[1] + "\t" + splits[2]);
            }
        }

        Files.write(outputPath, outputLines, StandardCharsets.UTF_8);
    }
}
