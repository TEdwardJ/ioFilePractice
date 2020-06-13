package edu.ted.analizer;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class SentenceReaderTest {
    @Test
    void readLine() {
        File testFile = new File(getClass().getClassLoader().getResource("FA.txt").getFile());
        try (FileReader fileReader = new FileReader(testFile);
             BufferedReader bufReader = new BufferedReader(fileReader);
             BufferedReader sentenceReader = new SentenceReader(bufReader)) {
            String line;
            while ((line = sentenceReader.readLine()) != null) {
                assertTrue(line.length() > 0);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}