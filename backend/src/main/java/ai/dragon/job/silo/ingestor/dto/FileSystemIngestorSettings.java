package ai.dragon.job.silo.ingestor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileSystemIngestorSettings {
    public static final String DEFAULT_PATH_MATCHER = "glob:*.{txt,pdf,doc,docx,ppt,pptx,xls,xlsx}";

    private String path;
    private String pathMatcher;
    private boolean recursive;

    public FileSystemIngestorSettings() {
        recursive = false;
        pathMatcher = "glob:*.{txt,pdf,doc,docx,ppt,pptx,xls,xlsx}";
    }
}
