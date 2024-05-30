package ai.dragon.job.silo.ingestor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import ai.dragon.entity.SiloEntity;

public abstract class ImplAbstractSiloIngestor implements AbstractSiloIngestor {
    protected SiloEntity entity;

    private ImplAbstractSiloIngestor() throws Exception {
    }

    public ImplAbstractSiloIngestor(SiloEntity entity) throws Exception {
        this();
        this.entity = entity;
        checkIngestorSettings();
    }

    protected <T> T convertIniSettingsToObject(List<String> settings, Class<T> clazz) throws Exception {
        Map<String, String> settingsMap = new HashMap<String, String>();
        for (String setting : settings) {
            String[] parts = setting.split("=", 2);
            if (parts.length <= 1) {
                throw new Exception(String.format("Invalid setting '%s' for class '%s'", setting, clazz.getName()));
            }
            String key = parts[0].trim();
            String value = parts[1].trim();
            settingsMap.put(key, value);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(settingsMap, clazz);
    }
}
