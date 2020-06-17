package edu.ted.analizer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileAnalyzer {

    private final String sourceFile;
    private final String word;

    public FileAnalyzer(String sourceFile, String word) {
        this.sourceFile = sourceFile;
        this.word = word;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Two arguments are required");
        }
        String sourceFile = args[0];
        String word = args[1];

        String report = new FileAnalyzer(sourceFile, word).execute();
        System.out.println(report);
    }

    private String execute() {
        String fileText = getSourceText(sourceFile);
        List<String> sentenceList = splitToSentences(fileText);
        Map<String, Integer> filteredSentences = filterSentences(sentenceList, word);

        return getReportStatistics(word, filteredSentences, sourceFile);
    }

    protected static String getReportStatistics(String word, Map<String, Integer> filteredResultsMap, String sourceFile) {
        StringBuilder report = new StringBuilder();
        int totalCounter = 0;
        for (Map.Entry<String, Integer> entry : filteredResultsMap.entrySet()) {
            report
                    .append(entry.getValue())
                    .append(" time(s) in the sentence: ")
                    .append(entry.getKey())
                    .append("\n");
            totalCounter += entry.getValue();
        }
        report.insert(0, "The word " + word + " has been found " + totalCounter + " times in the file: " + sourceFile + ":\n");
        return report.toString();
    }

    protected static Map<String, Integer> filterSentences(List<String> sentenceList, String word) {
        Pattern wordPattern = Pattern.compile("(?i)[ ]{1,}(" + word + ")[:; ]{1,}");
        Map<String, Integer> filteredResultsMap = new HashMap<>();
        for (String sentence : sentenceList) {
            Matcher wordMatcher = wordPattern.matcher(sentence);
            if (wordMatcher.find()) {
                filteredResultsMap.put(sentence, wordMatcher.groupCount());
            }
        }
        return filteredResultsMap;
    }

    protected static List<String> splitToSentences(String fileText) {
        List<String> sentencesList;
        try (SentenceReader sentenceReader = new SentenceReader(new StringReader(fileText))) {
            String sentence;
            sentencesList = new ArrayList<>();
            while ((sentence = sentenceReader.readSentence()) != null) {
                sentencesList.add(sentence);
            }
            return sentencesList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String getSourceText(String sourceFilePath) {
        File sourceFile = new File(sourceFilePath);
        StringBuilder text = new StringBuilder();
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile))) {
            while ((line = reader.readLine()) != null) {
                text.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    protected String getSourceText() {
        return getSourceText(sourceFile);
    }
}
