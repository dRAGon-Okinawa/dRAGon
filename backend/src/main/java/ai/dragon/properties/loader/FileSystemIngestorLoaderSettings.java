package ai.dragon.properties.loader;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileSystemIngestorLoaderSettings extends DefaultIngestorLoaderSettings {
    public static final String DEFAULT_PATH_MATCHER = "glob:**.{txt,pdf,doc,docx,ppt,pptx,xls,xlsx}";

    private List<String> paths;
    private String pathMatcher;
    private boolean recursive;

    public FileSystemIngestorLoaderSettings() {
        super();
        recursive = false;
        pathMatcher = DEFAULT_PATH_MATCHER;
    }
}
