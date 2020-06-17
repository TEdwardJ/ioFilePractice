package edu.ted.analizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SentenceReader extends BufferedReader {

    private static final Pattern SENTENCE_DELIMITER_PATTERN = Pattern.compile("(?m)([.!?][ ]{0,}|.\\Z)");

    private final StringBuilder sentenceBuffer = new StringBuilder();

    public SentenceReader(Reader in) {
        super(in);
    }

    public String readSentence() throws IOException {
        String sentence;
        if ((sentence = extractSentence()) != null) {
            return sentence;
        }
        int count;
        char[] charBuffer = new char[1000];
        while ((count = read(charBuffer)) > 0) {
            if (count < charBuffer.length) {
                sentenceBuffer.append(Arrays.copyOfRange(charBuffer, 0, count));
            } else {
                sentenceBuffer.append(charBuffer);
            }
            if ((sentence = extractSentence()) != null) {
                return sentence;
            }
        }
        return null;
    }

    private String extractSentence() {
        Matcher sentenceDelimiterMatcher = SENTENCE_DELIMITER_PATTERN.matcher(sentenceBuffer);
        if (!sentenceDelimiterMatcher.find()) {
            return null;
        }
        int sentenceEndPosition = sentenceDelimiterMatcher.end();
        String sentence = sentenceBuffer
                .substring(0, sentenceEndPosition)
                .replaceAll("([\\n\\r])", "");
        sentenceBuffer.delete(0, sentenceEndPosition);
        return sentence;
    }
}
