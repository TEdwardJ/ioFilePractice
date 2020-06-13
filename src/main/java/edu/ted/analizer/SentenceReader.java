package edu.ted.analizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SentenceReader extends BufferedReader {

    private static final Pattern sentenceDelimiterPattern = Pattern.compile("(?m)([.!?][ ]{0,}|.\\Z)");

    private final StringBuilder sentenceBuffer = new StringBuilder();

    private final Reader in;

    private final char[] charBuffer = new char[1000];

    public SentenceReader(Reader in) {
        super(in);
        this.in = in;
    }

    public String readSentence() throws IOException {
        int size;
        String sentence;

        if ((sentence = extractSentence()) != null) {
            return sentence;
        }
        while ((size = in.read(charBuffer)) > 0) {
            if (size < charBuffer.length) {
                sentenceBuffer.append(Arrays.copyOfRange(charBuffer, 0, size));
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
        Matcher sentenceDelimiterMatcher = sentenceDelimiterPattern.matcher(sentenceBuffer);
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

    @Override
    public void close() throws IOException {
        super.close();
    }
}
