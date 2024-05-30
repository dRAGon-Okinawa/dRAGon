package ai.dragon.job.silo.ingestor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalIngestorSettings {
    private String localPath;
}
