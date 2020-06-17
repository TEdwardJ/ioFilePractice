package edu.ted.analizer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FileAnalyzerTest {

    private static final List<String> TEST_SENTENCE_LIST = new ArrayList<>(4);
    private static final StringBuilder TEXT_TEXT = new StringBuilder();

    private final ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorTestOutput = new ByteArrayOutputStream();
    private final PrintStream originalOutput = System.out;
    private final PrintStream errorOriginalOutput = System.err;

    private final String testSourceFilePath = System.getProperty("user.dir") + "/src/test/resources/FA.txt";

    private FileAnalyzer analyzer;

    @BeforeAll
    public static void generalInit(){
        TEST_SENTENCE_LIST.add("Hash table based implementation of the Map interface. ");
        TEST_SENTENCE_LIST.add("This implementation provides all of the optional map operations, and permits null values and the null key. ");
        TEST_SENTENCE_LIST.add("The HashMap class is roughly equivalent to Hashtable, except that it is unsynchronized and permits nulls.");
        TEST_SENTENCE_LIST.add("This class makes no guarantees as to the order of the map; in particular, it does not guarantee that the order will remain constant over time.");
        TEST_SENTENCE_LIST.forEach(TEXT_TEXT::append);
    }

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
    void givenFileAnalyzerStartStaticMain_whenResults_thenCorrect() {
        setUpStreams();
        FileAnalyzer.main(new String[]{testSourceFilePath, "ghost"});
        restoreStreams();
        System.out.println(testOutput.toString());
        assertTrue(testOutput.toString().contains("The word ghost has been found 4 times in the file"));
    }

    @Test
    void givenFileAnalyzerStartStaticMain_whenNotEnoughParameters_thenException() {
        setUpStreams();
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> FileAnalyzer.main(new String[]{testSourceFilePath}));
        assertEquals("Two arguments are required", thrown.getMessage());
    }

    @Test
    void givenAllResultStatistics_whenReportWellFormed_thenCorrect() {
        Map<String, Integer> reportResults = new HashMap<>();
        String fileName = "File.txt";
        String wordToFind = "Map";
        reportResults.put("Hash table based implementation of the Map interface.", 1);
        reportResults.put("This implementation of Map provides all of the optional map operations, and permits null values and the null key. ", 2);
        String testStatistics = FileAnalyzer.getReportStatistics(wordToFind, reportResults, fileName);
        assertTrue(testStatistics.length() > 0);
        assertTrue(testStatistics.contains("The word " + wordToFind + " has been found 3 times in the file: " + fileName + ":"));
        for (Map.Entry<String, Integer> entry : reportResults.entrySet()) {
            assertTrue(testStatistics.contains("in the sentence: "+entry.getKey()));
        }
    }

    @Test
    void givenListOfSentences_whenFilteredByWord_thenCorrect() {
        Map<String, Integer> filteredSentenceMap = FileAnalyzer.filterSentences(TEST_SENTENCE_LIST, "map");

        assertEquals(3, filteredSentenceMap.keySet().size());
        assertTrue(filteredSentenceMap.containsKey(TEST_SENTENCE_LIST.get(0)));
        assertEquals(1, filteredSentenceMap.get(TEST_SENTENCE_LIST.get(0)));
        assertFalse(filteredSentenceMap.containsKey(TEST_SENTENCE_LIST.get(2)));
        assertNull(filteredSentenceMap.get(TEST_SENTENCE_LIST.get(2)));
    }

    @Test
    void givenText_whenSplitIntoSentences_thenCorrect() {
        List<String> sentenceList = FileAnalyzer.splitToSentences(TEXT_TEXT.toString());
        assertEquals(4, sentenceList.size());
        for (String sentence : sentenceList) {
            assertTrue(sentence.length() > 0);
        }
        for (String inputSentence : TEST_SENTENCE_LIST) {
            assertTrue(sentenceList.contains(inputSentence));
        }
    }

    @Test
    void givenTextFile_whenTextIsRead_thenCorrect() {
        String text = analyzer.getSourceText();
        assertTrue(text.length() > 0);
        assertTrue(text.startsWith("The ghost stared angrily at her"));
        assertTrue(text.endsWith("and he loved it"));
    }
}