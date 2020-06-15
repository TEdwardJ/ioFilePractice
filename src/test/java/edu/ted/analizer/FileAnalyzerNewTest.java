package edu.ted.analizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FileAnalyzerTest {

    private final ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorTestOutput = new ByteArrayOutputStream();
    private final PrintStream originalOutput = System.out;
    private final PrintStream errorOriginalOutput = System.err;

    String testSourceFilePath = System.getProperty("user.dir") + "/src/test/resources/FA.txt";
    private FileAnalyzer analyzer;
    private String testText = "Hash table based implementation of the Map interface. " +
            "This implementation provides all of the optional map operations, and permits null values and the null key. " +
            "The HashMap class is roughly equivalent to Hashtable, except that it is unsynchronized and permits nulls." +
            "This class makes no guarantees as to the order of the map; in particular, it does not guarantee that the order will remain constant over time.";
    ;

    @BeforeEach
    public void init() {
        analyzer = new FileAnalyzer(testSourceFilePath, "ghost");
    }


    public void setUpStreams() {
        System.setOut(new PrintStream(testOutput));
        System.setErr(new PrintStream(errorTestOutput));
    }

    public void restoreStreams() {
        System.setOut(originalOutput);
        System.setErr(errorOriginalOutput);
    }

    @Test
    void main() {
        setUpStreams();
        FileAnalyzer.main(new String[]{testSourceFilePath, "ghost"});
        restoreStreams();
        System.out.println(testOutput.toString());
        assertTrue(testOutput.toString().contains("The word ghost has been found 4 times in the file"));
    }

    @Test
    void mainWithBadArguments() {
        setUpStreams();
        Throwable thrown = assertThrows(IllegalArgumentException.class,()-> FileAnalyzer.main(new String[]{testSourceFilePath}));
        assertEquals("Two arguments are required", thrown.getMessage());
    }

    @Test
    void execute() {
    }

    @Test
    void getReportStatistics() {
        Map<String, Integer> testResults = new HashMap<>();
        String fileName = "File.txt";
        String wordToFind = "Map";
        testResults.put("Hash table based implementation of the Map interface.", 1);
        testResults.put("This implementation of Map provides all of the optional map operations, and permits null values and the null key. ", 2);
        String testStatistics = analyzer.getReportStatistics(wordToFind, testResults, fileName);
        assertTrue(testStatistics.length() > 0);
        assertTrue(testStatistics.contains("The word " + wordToFind + " has been found 3 times in the file: " + fileName + ":"));
        System.out.println(testStatistics);
    }

    @Test
    void filterSentences() {
        List<String> sentenceList = new ArrayList<>();
        sentenceList.add("Hash table based implementation of the Map interface.");
        sentenceList.add("This implementation provides all of the optional map operations, and permits null values and the null key. ");
        sentenceList.add("The HashMap class is roughly equivalent to Hashtable, except that it is unsynchronized and permits nulls.");
        Map<String, Integer> filteredSentenceList = analyzer.filterSentences(sentenceList, "map");
        assertTrue(filteredSentenceList.containsKey("Hash table based implementation of the Map interface."));
        assertEquals(1, filteredSentenceList.get("Hash table based implementation of the Map interface."));
        assertFalse(filteredSentenceList.containsKey("The HashMap class is roughly equivalent to Hashtable, except that it is unsynchronized and permits nulls."));
        assertNull(filteredSentenceList.get("The HashMap class is roughly equivalent to Hashtable, except that it is unsynchronized and permits nulls."));
    }

    @Test
    void splitToSentences() {
        List<String> sentenceList = analyzer.splitToSentences(testText);
        assertFalse(sentenceList.isEmpty());
        for (String sentence : sentenceList) {
            assertTrue(sentence.length() > 0);
        }
        assertEquals(4, sentenceList.size());
    }

    @Test
    void getSourceFileText() {
        File testSourceFile = new File(testSourceFilePath);
        String text = analyzer.getSourceFileText();
        assertTrue(text.length() > 0);
        assertTrue(text.startsWith("The ghost stared angrily at her"));
        assertTrue(text.endsWith("and he loved it"));
    }
}