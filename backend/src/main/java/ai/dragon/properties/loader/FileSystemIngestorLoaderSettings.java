package ai.dragon.properties.loader;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileSystemIngestorLoaderSettings {
    public static final String DEFAULT_PATH_MATCHER = "glob:**.{txt,pdf,doc,docx,ppt,pptx,xls,xlsx}";

    private List<String> paths;
    private String pathMatcher;
    private boolean recursive;

    public FileSystemIngestorLoaderSettings() {
        recursive = false;
        pathMatcher = DEFAULT_PATH_MATCHER;
    }
}
