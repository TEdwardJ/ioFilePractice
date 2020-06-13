package edu.ted.analizer;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileAnalizer {
    private final String sourceFile;
    private final String word;
    private final Pattern wordPattern;

    public FileAnalizer(String sourceFile, String word) {
        this.sourceFile = sourceFile;
        this.word = word;
        wordPattern = Pattern.compile("(?i)(" + word + ")");
    }

    public static void main(String[] args){
        if (args.length < 2) {
            throw new IllegalArgumentException("2 arguments are required");
        }
        String sourceFile = args[0];
        String word = args[1];

        FileAnalizer analizer = new FileAnalizer(sourceFile, word);
        analizer.analize();
    }

    public void analize() {
        int totalCounter = 0;
        try (FileReader fileReader = new FileReader(sourceFile);
             BufferedReader bufferReader = new BufferedReader(fileReader);
             SentenceReader sentenceReader = new SentenceReader(bufferReader)) {
            String sentence;
            while ((sentence = sentenceReader.readSentence()) != null) {
                Matcher wordMatcher = wordPattern.matcher(sentence);
                if (wordMatcher.find()) {
                    int foundInTheSentence = wordMatcher.groupCount();
                    totalCounter += foundInTheSentence;
                    if (foundInTheSentence > 0) {
                        System.out.println(sentence);
                    }
                }
            }
            System.out.println("The word " + word + " met " + totalCounter + " times in the file: " + sourceFile);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
