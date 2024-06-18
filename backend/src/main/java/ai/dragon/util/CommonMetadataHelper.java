package ai.dragon.util;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

import dev.langchain4j.data.document.Document;

public class CommonMetadataHelper {
    public static void updateWithFile(Document document, File file) {
        document.metadata()
                .put("document_name", file.getName())
                .put("document_date", file.lastModified())
                .put("document_size", file.length())
                .put("document_location", file.getAbsolutePath());
    }

    public static void updateWithURL(Document document, URL url) {
        document.metadata()
                .put("document_name", Paths.get(url.getPath()).getFileName().toString())
                .put("document_date", System.currentTimeMillis()) // TODO
                .put("document_size", 0) // TODO
                .put("document_location", url.toString());
    }
}
