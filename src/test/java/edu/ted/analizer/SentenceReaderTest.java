package edu.ted.analizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SentenceReaderTest {

    private final List<String> testStringList = new ArrayList<>(4);

    private final StringBuilder testText = new StringBuilder();

    @BeforeEach
    public void init(){
        testStringList.add("Hash table based implementation of the Map interface. ");
        testStringList.add("This implementation provides all of the optional map operations, and permits null values and the null key. ");
        testStringList.add("The HashMap class is roughly equivalent to Hashtable, except that it is unsynchronized and permits nulls.");
        testStringList.add("This class makes no guarantees as to the order of the map; in particular, it does not guarantee that the order will remain constant over time.");
        testStringList.forEach(testText::append);
    }

    @Test
    void readLine() {
        try (SentenceReader sentenceReader = new SentenceReader(new StringReader(testText.toString()))) {
            String line;
            int counter = 0;
            while ((line = sentenceReader.readSentence()) != null) {
                assertTrue(line.length() > 0);
                assertEquals(testStringList.get(counter), line);
                counter++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}