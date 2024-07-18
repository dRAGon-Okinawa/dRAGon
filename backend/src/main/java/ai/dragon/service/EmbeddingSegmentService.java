package ai.dragon.service;

import org.springframework.stereotype.Service;

@Service
public class EmbeddingSegmentService {
    public String cleanTextSegment(String textSegment) {
        if (textSegment == null) {
            return null;
        }
        // https://stackoverflow.com/a/15495127/8102448
        return textSegment
                // 1. compress all non-newline whitespaces to single space
                .replaceAll("[\\s&&[^\\n]]+", " ")
                // 2. remove spaces from beginning or end of lines
                .replaceAll("(?m)^\\s|\\s$", "")
                // 3. compress multiple newlines to single newlines
                .replaceAll("\\n+", "\n")
                // 4. remove newlines from beginning or end of string
                .replaceAll("^\n|\n$", "");
    }
}
