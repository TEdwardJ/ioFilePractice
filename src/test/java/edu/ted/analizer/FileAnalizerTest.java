package edu.ted.analizer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

class FileAnalizerTest {
    private final ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorTestOutput = new ByteArrayOutputStream();
    private final PrintStream originalOutput = System.out;
    private final PrintStream errorOriginalOutput = System.err;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(testOutput));
        System.setErr(new PrintStream(errorTestOutput));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOutput);
        System.setErr(errorOriginalOutput);
    }

    @Test
    void analize() throws IOException {
        File testFile = new File(getClass().getClassLoader().getResource("FA.txt").getFile());
        FileAnalizer analizer = new FileAnalizer(testFile.getAbsolutePath(),"ghost");
        analizer.analize();
        assertTrue(testOutput.toString().contains("The word ghost met 4 times in the file"));
    }
}